package com.ttenushko.mvi

import java.util.concurrent.CopyOnWriteArraySet

@Suppress("SpellCheckingInspection")
internal class MviStoreImpl<I, A, S, E>(
    initialState: S,
    private val intentToActionConverter: IntentToActionConverter<I, A>? = null,
    private val bootstrapper: MviBootstrapper<A, S, E>? = null,
    middleware: Collection<MviMiddleware<A, S, E>>? = null,
    reducer: MviReducer<A, S>
) : MviStore<I, S, E> {

    companion object {
        private const val STATUS_IDLE = 0
        private const val STATUS_RUNNING = 1
        private const val STATUS_CLOSED = 2
    }

    @Volatile
    override var state: S = initialState
    private val middlewareChain: MviMiddlewareChain<A, S, E>
    private val stateChangedListeners =
        CopyOnWriteArraySet<MviStore.StateChangedListener<S>>()
    private val eventListeners =
        CopyOnWriteArraySet<MviStore.EventListener<E>>()
    private val messageQueueDrain: ValueQueueDrain<Message<A, E>> =
        ValueQueueDrain { message ->
            when (message) {
                is Message.Bootstrap -> processBootstrap()
                is Message.Action<A> -> processAction(message.value)
                is Message.Event<E> -> processEvent(message.value)
                is Message.Close -> processClose()
            }
        }
    private val actionDispatcher = object : Dispatcher<A> {
        override fun dispatch(value: A) {
            messageQueueDrain.drain(Message.Action(value))
        }
    }
    private val eventDispatcher = object : Dispatcher<E> {
        override fun dispatch(value: E) {
            messageQueueDrain.drain(Message.Event(value))
        }
    }
    private val stateProvider = object : Provider<S> {
        override fun get(): S {
            return state
        }
    }

    @Volatile
    private var status = STATUS_IDLE
    private val closeHandler = CloseHandler {
        messageQueueDrain.drain(Message.Close)
    }


    init {
        val reducerMiddleware = ReducerMiddleware(reducer) { updateState(it) }
        middlewareChain =
            MviMiddlewareChain(middleware?.plus(reducerMiddleware) ?: listOf(reducerMiddleware))
    }

    override fun run() {
        closeHandler.checkNotClosed()
        messageQueueDrain.drain(Message.Bootstrap)
    }

    override fun dispatchIntent(intent: I) {
        closeHandler.checkNotClosed()
        if (null != intentToActionConverter) {
            actionDispatcher.dispatch(intentToActionConverter.convert(intent))
        } else noIntentToActionConverter()
    }

    override fun addStateChangedListener(listener: MviStore.StateChangedListener<S>) {
        closeHandler.checkNotClosed()
        stateChangedListeners.add(listener)
    }

    override fun removeStateChangedListener(listener: MviStore.StateChangedListener<S>) {
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

    private fun processBootstrap() {
        when (status) {
            STATUS_IDLE -> {
                try {
                    bootstrapper?.bootstrap(state, actionDispatcher, eventDispatcher)
                } catch (error: Throwable) {
                    throw RuntimeException("Error occurred while bootstrap.", error)
                }
                status = STATUS_RUNNING
            }
            STATUS_RUNNING -> alreadyRunning()
            STATUS_CLOSED -> instanceClosed()
        }
    }

    private fun processAction(action: A) {
        when (status) {
            STATUS_IDLE -> notRunning()
            STATUS_RUNNING -> {
                try {
                    middlewareChain.apply(action, stateProvider, actionDispatcher, eventDispatcher)
                } catch (error: Throwable) {
                    throw RuntimeException("Error occurred while processing action.", error)
                }
            }
            STATUS_CLOSED -> instanceClosed()
        }
    }

    private fun processEvent(event: E) {
        when (status) {
            STATUS_IDLE -> notRunning()
            STATUS_RUNNING -> {
                eventListeners.forEach { eventListener ->
                    try {
                        eventListener.onEvent(event)
                    } catch (error: Throwable) {
                        throw RuntimeException("Error occurred while handling event.", error)
                    }
                }
            }
            STATUS_CLOSED -> instanceClosed()
        }
    }

    private fun processClose() {
        when (status) {
            STATUS_IDLE, STATUS_RUNNING -> {
                middlewareChain.close()
                status = STATUS_CLOSED
            }
            STATUS_CLOSED -> instanceClosed()
        }
    }

    private fun updateState(newState: S) {
        if (newState != this.state) {
            this.state = newState
            stateChangedListeners.forEach { stateChangedListener ->
                try {
                    stateChangedListener.onStateChanged(newState)
                } catch (error: Throwable) {
                    throw RuntimeException("Error occured while updating state.", error)
                }
            }
        }
    }

    private fun noIntentToActionConverter(): Nothing =
        throw IllegalStateException("Intent to action converter is not provided.")

    private fun notRunning(): Nothing =
        throw IllegalStateException("This instance is not running yet. Call 'run()' first.")

    private fun alreadyRunning(): Nothing =
        throw IllegalStateException("This instance is already running. No need to call 'run()' multiple times.")

    private fun instanceClosed(): Nothing =
        throw IllegalStateException("This instance is closed.")

    private inner class ReducerMiddleware(
        private val reducer: MviReducer<A, S>,
        private val updateState: (S) -> Unit
    ) : MviMiddleware<A, S, E> {

        private val closeHandler = CloseHandler {
            // do nothing
        }

        override fun apply(chain: MviMiddleware.Chain<A, S, E>) {
            closeHandler.checkNotClosed()
            updateState(reducer.reduce(chain.action, chain.state))
        }

        override fun close() {
            closeHandler.close()
        }
    }

    private sealed class Message<out A, out E> {
        object Bootstrap : Message<Nothing, Nothing>()
        object Close : Message<Nothing, Nothing>()
        class Action<A>(val value: A) : Message<A, Nothing>()
        class Event<E>(val value: E) : Message<Nothing, E>()
    }
}