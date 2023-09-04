package com.ttenushko.mvi

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicBoolean


internal class MviStoreImpl<I, A, S, E>(
    initialState: S,
    private val intentToActionConverter: ((I) -> A?)? = null,
    private val bootstrapper: MviBootstrapper<A, S, E>? = null,
    middleware: Collection<MviMiddleware<A, S, E>>? = null,
    reducer: MviReducer<A, S, E>,
    coroutineDispatcher: CoroutineDispatcher
) : MviStore<I, S, E> {

    override val isRunning: Boolean
        get() {
            return synchronized(internalStateLock) {
                !closeHandler.isClosed && InternalState.Running == internalState
            }
        }

    override val isClosed: Boolean
        get() {
            return closeHandler.isClosed
        }

    @Volatile
    override var state: S = initialState
    private val internalStateLock = Any()
    private var internalState: InternalState = InternalState.Idle
    private val coroutineScope = CoroutineScope(coroutineDispatcher + Job())
    private val messageChannel = Channel<Message<A, E>>(capacity = Channel.UNLIMITED)
    private val provideState: Provider<S> = { state }
    private val dispatchAction: Dispatcher<A> = { action -> sendMessage(Message.Action(action)) }
    private val dispatchEvent: Dispatcher<E> = { event -> sendMessage(Message.Event(event)) }
    private val middlewareChain: MviMiddlewareChain<A, S, E> =
        ReducerMiddleware(reducer, ::updateState).let { reducerMiddleware ->
            MviMiddlewareChain(
                middleware = middleware?.plus(reducerMiddleware) ?: listOf(reducerMiddleware),
                provideState = provideState,
                dispatchAction = dispatchAction,
                dispatchEvent = dispatchEvent
            )
        }
    private val stateChangedListeners = CopyOnWriteArraySet<MviStore.StateChangedListener>()
    private val eventListeners = CopyOnWriteArraySet<MviStore.EventListener<E>>()
    private val closeHandler = CloseHandler {
        messageChannel.trySend(Message.Terminate)
    }

    override fun run() {
        synchronized(internalStateLock) {
            closeHandler.checkNotClosed()
            when (internalState) {
                InternalState.Idle -> {
                    coroutineScope.launch {
                        val bootstrapperCalled = AtomicBoolean(false)
                        messageChannel.consumeAsFlow().collect { message ->
                            processMessage(message, bootstrapperCalled)
                        }
                    }.also { job ->
                        job.invokeOnCompletion {
                            middlewareChain.close()
                            messageChannel.close()
                        }
                    }
                    internalState = InternalState.Running
                }

                InternalState.Running -> throw IllegalStateException("This instance is already running. No need to call 'run()' multiple times.")
            }
        }.also {
            sendMessage(Message.Bootstrap)
        }
    }

    override fun dispatchIntent(intent: I) {
        synchronized(internalStateLock) {
            closeHandler.checkNotClosed()
            when (internalState) {
                InternalState.Idle -> throw IllegalStateException("This instance is not running yet. Call 'run()' first.")
                InternalState.Running -> Unit
            }
        }.also {
            intentToActionConverter?.invoke(intent)?.let { action ->
                sendMessage(Message.Action(action))
            }
        }
    }

    override fun addStateChangedListener(listener: MviStore.StateChangedListener) {
        closeHandler.checkNotClosed()
        stateChangedListeners.add(listener)
    }

    override fun removeStateChangedListener(listener: MviStore.StateChangedListener) {
        stateChangedListeners.remove(listener)
    }

    override fun addEventListener(listener: MviStore.EventListener<E>) {
        closeHandler.checkNotClosed()
        eventListeners.add(listener)
    }

    override fun removeEventListener(listener: MviStore.EventListener<E>) {
        eventListeners.remove(listener)
    }

    override fun close() {
        closeHandler.close()
    }

    private fun processMessage(message: Message<A, E>, bootstrapperCalled: AtomicBoolean) {
        if (bootstrapperCalled.compareAndSet(false, true)) {
            bootstrapper?.bootstrap(state, dispatchAction, dispatchEvent)
        }
        when (message) {
            is Message.Terminate -> {
                coroutineScope.cancel()
            }

            is Message.Bootstrap -> {
                /* do nothing since bootstrapper must be called above */
            }

            is Message.Action<A> -> {
                middlewareChain.apply(message.value)
            }

            is Message.Event<E> -> {
                eventListeners.forEach { it.onEvent(message.value) }
            }
        }
    }

    private fun updateState(newState: S) {
        if (newState != state) {
            state = newState
            stateChangedListeners.forEach { it.onStateChanged() }
        }
    }

    private fun sendMessage(message: Message<A, E>) {
        messageChannel.trySend(message)
    }

    private class ReducerMiddleware<A, S, E>(
        private val reducer: MviReducer<A, S, E>,
        private val updateState: (S) -> Unit
    ) : MviMiddlewareImpl<A, S, E>() {
        override fun onApply(action: A, chain: MviMiddleware.Chain) {
            updateState(reducer.reduce(action, state, dispatchEvent))
        }

        override fun onClose() {
            // do nothing
        }
    }

    private sealed class InternalState {
        object Idle : InternalState()
        object Running : InternalState()
    }

    private sealed class Message<out A, out E> {
        object Terminate : Message<Nothing, Nothing>()
        object Bootstrap : Message<Nothing, Nothing>()
        class Action<A>(val value: A) : Message<A, Nothing>()
        class Event<E>(val value: E) : Message<Nothing, E>()
    }
}