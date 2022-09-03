package com.ttenushko.mvi

import java.io.Closeable
import java.util.concurrent.atomic.AtomicLong

internal class MviMiddlewareChain<A, S, E>(
    middleware: List<MviMiddleware<A, S, E>>,
    provideState: Provider<S>,
    dispatchAction: Dispatcher<A>,
    dispatchEvent: Dispatcher<E>
) : Closeable {

    private val chainData = MutableChainData<A, S>()
    private val chain: ChainItem<A, S, E> = requireNotNull(
        middleware.foldRight(null as ChainItem<A, S, E>?) { middleware, nextChainItem ->
            ChainItem(middleware, nextChainItem, chainData)
        }) { "Can not create middleware chain." }
    private val closeHandler: CloseHandler = CloseHandler {
        chain.close()
    }

    init {
        middleware.forEach { it.setup(provideState, dispatchAction, dispatchEvent) }
    }

    fun apply(action: A) {
        closeHandler.checkNotClosed()
        chainData.set(action)
        chain.apply()
        chainData.clear()
    }

    override fun close() {
        closeHandler.close()
    }


    private class ChainItem<A, S, E>(
        private val middleware: MviMiddleware<A, S, E>,
        private val next: ChainItem<A, S, E>?,
        private val chainData: ChainData<A, S>
    ) : MviMiddleware.Chain, Closeable {

        private var lastApplySequenceId: Long? = null
        private var lastProceedSequenceId: Long? = null
        private val closeHandler = CloseHandler {
            middleware.close()
            next?.close()
        }

        fun apply() {
            closeHandler.checkNotClosed()
            val sequenceId = chainData.sequenceId
            if (lastApplySequenceId != sequenceId) {
                lastApplySequenceId = sequenceId
                middleware.apply(chainData.action, this)
            } else throw IllegalStateException("apply() is called multiple times in a row")
        }

        override fun proceed() {
            closeHandler.checkNotClosed()
            if (null != next) {
                val sequenceId = chainData.sequenceId
                if (lastProceedSequenceId != sequenceId) {
                    lastProceedSequenceId = sequenceId
                    next.apply()
                } else throw IllegalStateException("proceed() is called multiple times in a row")
            } else throw IllegalStateException("There is no next item in chain")
        }

        override fun close() {
            closeHandler.close()
        }
    }


    private interface ChainData<A, S> {
        val action: A
        val sequenceId: Long
    }


    private class MutableChainData<A, S> : ChainData<A, S> {
        private var actionInt: A? = null
        private val sequenceIdInt = AtomicLong(0)
        override val action: A
            get() {
                return requireNotNull(actionInt) { "Action is not set" }
            }
        override val sequenceId: Long
            get() {
                return sequenceIdInt.get()
            }

        fun set(action: A) {
            sequenceIdInt.incrementAndGet()
            this.actionInt = action
        }

        fun clear() {
            sequenceIdInt.incrementAndGet()
            this.actionInt = null
        }
    }
}