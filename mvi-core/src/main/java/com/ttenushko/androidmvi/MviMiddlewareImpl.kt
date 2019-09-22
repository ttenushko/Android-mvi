package com.ttenushko.androidmvi

abstract class MviMiddlewareImpl<A, S, E> : MviMiddleware<A, S, E> {

    private var _stateProvider: Provider<S>? = null
    private var _actionDispatcher: Dispatcher<A>? = null
    private var _eventDispatcher: Dispatcher<E>? = null
    protected val stateProvider: Provider<S>
        get() = _stateProvider
            ?: throw IllegalStateException("State provider is not initialized yet")
    protected val actionDispatcher: Dispatcher<A>
        get() = _actionDispatcher
            ?: throw IllegalStateException("Action dispatcher is not initialized yet")
    protected val eventDispatcher: Dispatcher<E>
        get() = _eventDispatcher
            ?: throw IllegalStateException("Event dispatcher is not initialized yet")

    override fun apply(chain: MviMiddleware.Chain<A, S, E>) {
        if (null == _stateProvider || _stateProvider != chain.stateProvider) {
            _stateProvider = chain.stateProvider
        }
        if (null == _actionDispatcher || _actionDispatcher != chain.actionDispatcher) {
            _actionDispatcher = chain.actionDispatcher
        }
        if (null == _eventDispatcher || _eventDispatcher != chain.eventDispatcher) {
            _eventDispatcher = chain.eventDispatcher
        }
    }
}