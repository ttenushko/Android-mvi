package com.ttenushko.androidmvi

class LoggingMiddleware<A, S, E>(
    private val logger: Logger<A, S>
) : MviMiddleware<A, S, E> {

    override fun apply(chain: MviMiddleware.Chain<A, S, E>) {
        val action = chain.action
        val oldState = chain.stateProvider.get()
        val newState = chain.proceed().let { chain.stateProvider.get() }
        logger.log(action, oldState, newState)
    }

    override fun close() {
        // do nothing
    }

    interface Logger<A, S> {
        fun log(action: A, oldState: S, newState: S)
    }
}