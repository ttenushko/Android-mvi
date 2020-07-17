package com.ttenushko.mvi

abstract class MviMiddlewareImpl<A, S, E> :
    MviMiddleware<A, S, E> {

    private lateinit var _actionDispatcher: Dispatcher<A>
    private lateinit var _eventDispatcher: Dispatcher<E>
    protected val actionDispatcher: Dispatcher<A>
        get() = _actionDispatcher
    protected val eventDispatcher: Dispatcher<E>
        get() = _eventDispatcher
    private val closeHandler = CloseHandler {
        onClose()
    }

    override fun apply(chain: MviMiddleware.Chain<A, S, E>) {
        closeHandler.checkNotClosed()
        if (!::_actionDispatcher.isInitialized || _actionDispatcher != chain.actionDispatcher) {
            _actionDispatcher = chain.actionDispatcher
        }
        if (!::_eventDispatcher.isInitialized || _eventDispatcher != chain.eventDispatcher) {
            _eventDispatcher = chain.eventDispatcher
        }
    }

    override fun close() {
        closeHandler.close()
    }

    protected abstract fun onClose()
}