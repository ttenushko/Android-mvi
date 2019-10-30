package com.ttenushko.androidmvi

import java.util.concurrent.atomic.AtomicBoolean

abstract class MviMiddlewareImpl<A, S, E> : MviMiddleware<A, S, E> {

    private lateinit var _stateProvider: Provider<S>
    private lateinit var _actionDispatcher: Dispatcher<A>
    private lateinit var _eventDispatcher: Dispatcher<E>
    protected val stateProvider: Provider<S>
        get() = _stateProvider
    protected val actionDispatcher: Dispatcher<A>
        get() = _actionDispatcher
    protected val eventDispatcher: Dispatcher<E>
        get() = _eventDispatcher
    private val isClosed = AtomicBoolean(false)

    override fun apply(chain: MviMiddleware.Chain<A, S, E>) {
        if (!::_stateProvider.isInitialized || _stateProvider != chain.stateProvider) {
            _stateProvider = chain.stateProvider
        }
        if (!::_actionDispatcher.isInitialized || _actionDispatcher != chain.actionDispatcher) {
            _actionDispatcher = chain.actionDispatcher
        }
        if (!::_eventDispatcher.isInitialized || _eventDispatcher != chain.eventDispatcher) {
            _eventDispatcher = chain.eventDispatcher
        }
    }

    override fun close() {
        if (isClosed.compareAndSet(false, true)) {
            onClose()
        }
    }

    protected abstract fun onClose()
}