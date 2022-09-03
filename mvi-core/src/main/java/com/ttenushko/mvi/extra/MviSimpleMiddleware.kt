package com.ttenushko.mvi.extra

import com.ttenushko.mvi.Dispatcher
import com.ttenushko.mvi.MviMiddleware
import com.ttenushko.mvi.MviMiddlewareImpl

public class MviSimpleMiddleware<A, S, E>(
    private val applyHandler: MviMiddlewareScope<A, S, E>.(action: A, chain: MviMiddleware.Chain) -> Unit
) : MviMiddlewareImpl<A, S, E>() {

    private val mviMiddlewareScope = object : MviMiddlewareScope<A, S, E> {
        override val state: S
            get() {
                return this@MviSimpleMiddleware.state
            }
        override val dispatchAction: Dispatcher<A>
            get() {
                return this@MviSimpleMiddleware.dispatchAction
            }
        override val dispatchEvent: Dispatcher<E>
            get() {
                return this@MviSimpleMiddleware.dispatchEvent
            }
    }

    override fun onApply(action: A, chain: MviMiddleware.Chain) {
        applyHandler(mviMiddlewareScope, action, chain)
    }

    override fun onClose() {
        // do nothing
    }

    public interface MviMiddlewareScope<A, S, E> {
        public val state: S
        public val dispatchAction: Dispatcher<A>
        public val dispatchEvent: Dispatcher<E>
    }
}