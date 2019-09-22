package com.ttenushko.androidmvi

import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicInteger

object MviStores {
    fun <I, A, S, E> create(
        initialState: S,
        intentToActionTransformer: Transformer<I, A>? = null,
        bootstrapper: MviBootstrapper<A, S, E>? = null,
        middleware: Collection<MviMiddleware<A, S, E>>? = null,
        reducer: MviReducer<A, S>
    ): MviStore<I, S, E> =
        MviStoreImpl(
            initialState,
            intentToActionTransformer,
            bootstrapper,
            middleware,
            reducer
        )
}

@Suppress("SpellCheckingInspection")
private class MviStoreImpl<I, A, S, E>(
    initialState: S,
    private val intentToActionTransformer: Transformer<I, A>? = null,
    private val bootstrapper: MviBootstrapper<A, S, E>? = null,
    middlewares: Collection<MviMiddleware<A, S, E>>? = null,
    reducer: MviReducer<A, S>
) : MviStore<I, S, E> {

    companion object {
        private const val STATUS_UNINITIALIZED = 0
        private const val STATUS_RUNNING = 1
        private const val STATUS_CLOSED = 2
    }

    override var state: S = initialState
    private val middleware: List<MviMiddleware<A, S, E>>
    private val middlewareChain: MviMiddlewareChain<A, S, E>
    private val stateChangedListeners = CopyOnWriteArraySet<MviStore.StateChangedListener<S>>()
    private val eventListeners = CopyOnWriteArraySet<MviStore.EventListener<E>>()
    private val actionQueueDrain: ValueQueueDrain<A> =
        ValueQueueDrain { action -> processAction(action) }
    private val eventQueueDrain: ValueQueueDrain<E> =
        ValueQueueDrain { event -> processEvent(event) }
    private val actionDispatcher = object : Dispatcher<A> {
        override fun dispatch(value: A) {
            checkRunning()
            threadChecker.check()
            actionQueueDrain.drain(value)
        }
    }
    private val eventDispatcher = object : Dispatcher<E> {
        override fun dispatch(value: E) {
            checkRunning()
            threadChecker.check()
            eventQueueDrain.drain(value)
        }
    }
    private val stateProvider = object : Provider<S> {
        override fun get(): S {
            checkRunning()
            threadChecker.check()
            return state
        }
    }
    private val threadChecker =
        ThreadChecker("Only the original thread that created MVI Store can access it.")
    private val status = AtomicInteger(STATUS_UNINITIALIZED)


    init {
        middleware = middlewares?.plus(ReducerMiddleware(reducer))
            ?: listOf(ReducerMiddleware(reducer))
        middlewareChain = MviMiddlewareChain(middleware)
    }

    override fun run() {
        if (status.compareAndSet(STATUS_UNINITIALIZED, STATUS_RUNNING)) {
            bootstrapper?.bootstrap(state, actionDispatcher, eventDispatcher)
        }
    }

    override fun dispatchIntent(intent: I) {
        checkRunning()
        threadChecker.check()
        if (null != intentToActionTransformer) {
            actionDispatcher.dispatch(intentToActionTransformer.transform(intent))
        }
    }

    override fun addStateChangedListener(listener: MviStore.StateChangedListener<S>) {
        checkNotClosed()
        stateChangedListeners.add(listener)
    }

    override fun removeStateChangedListener(listener: MviStore.StateChangedListener<S>) {
        checkNotClosed()
        stateChangedListeners.remove(listener)
    }

    override fun addEventListener(listener: MviStore.EventListener<E>) {
        checkNotClosed()
        eventListeners.add(listener)
    }

    override fun removeEventListener(listener: MviStore.EventListener<E>) {
        checkNotClosed()
        eventListeners.remove(listener)
    }

    override fun close() {
        if (STATUS_RUNNING == status.getAndSet(STATUS_CLOSED)) {
            middleware.forEach { it.close() }
        }
    }

    private fun processAction(action: A) {
        checkRunning()
        threadChecker.check()
        middlewareChain.apply(action, stateProvider, actionDispatcher, eventDispatcher)
    }

    private fun processEvent(event: E) {
        checkRunning()
        threadChecker.check()
        eventListeners.forEach { eventListener ->
            eventListener.onEvent(event)
        }
    }

    private fun updateState(newState: S) {
        if (newState != this.state) {
            this.state = newState
            stateChangedListeners.forEach { stateChangedListener ->
                stateChangedListener.onStateChanged(newState)
            }
        }
    }

    private fun checkRunning() {
        when (status.get()) {
            STATUS_UNINITIALIZED -> throw IllegalStateException("This instance is not running yet.")
            STATUS_CLOSED -> throw IllegalStateException("This instance is already closed.")
        }
    }

    @Suppress("ReplaceGuardClauseWithFunctionCall")
    private fun checkNotClosed() {
        if (STATUS_CLOSED == status.get()) {
            throw IllegalStateException("This instance is already closed.")
        }
    }

    private inner class ReducerMiddleware(
        private val reducer: MviReducer<A, S>
    ) : MviMiddleware<A, S, E> {

        override fun apply(chain: MviMiddleware.Chain<A, S, E>) {
            val action = chain.action
            val currentState = chain.stateProvider.get()
            val newState = reducer.reduce(action, currentState)
            if (newState != currentState) {
                this@MviStoreImpl.updateState(newState)
            }
        }

        override fun close() {
            // do nothing
        }
    }
}