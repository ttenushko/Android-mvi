package com.ttenushko.mvi.extra

import com.ttenushko.mvi.MviMiddleware
import com.ttenushko.mvi.MviMiddlewareImpl


public class MviLoggingMiddleware<A, S, E>(
    private val logger: Logger<A, S>
) : MviMiddlewareImpl<A, S, E>() {

    override fun onApply(action: A, chain: MviMiddleware.Chain) {
        val oldState = state
        val newState = chain.proceed().let { state }
        logger.log(action, oldState, newState)
    }

    override fun onClose() {
        // do nothing
    }

    public fun interface Logger<A, S> {
        public fun log(action: A, oldState: S, newState: S)
    }
}

public fun <A, S, E> mviLoggingMiddleware(logger: MviLoggingMiddleware.Logger<A, S>): MviLoggingMiddleware<A, S, E> =
    MviLoggingMiddleware(logger)

public fun <A, S, E> mviLoggingMiddleware(logger: (action: A, oldState: S, newState: S) -> Unit): MviLoggingMiddleware<A, S, E> =
    MviLoggingMiddleware { action, oldState, newState -> logger(action, oldState, newState) }