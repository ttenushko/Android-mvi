package com.ttenushko.mvi

import java.io.Closeable
import java.util.concurrent.atomic.AtomicLong

internal class MviMiddlewareChain<A, S, E>(
    middleware: List<MviMiddleware<A, S, E>>
) : Closeable {

    private var sequence = AtomicLong(0)
    private val chainData = SettableChainData<A, S, E>()
    private val chain: ChainItem<A, S, E> =
        middleware.foldRight(null as ChainItem<A, S, E>?) { middleware, nextChainItem ->
            ChainItem(middleware, chainData, nextChainItem)
        }!!
    private val closeHandler = CloseHandler {
        chain.close()
    }

    fun apply(
        action: A,
        stateProvider: Provider<S>,
        actionDispatcher: Dispatcher<A>,
        eventDispatcher: Dispatcher<E>
    ) {
        closeHandler.checkNotClosed()
        chainData.set(
            action,
            stateProvider,
            actionDispatcher,
            eventDispatcher,
            sequence.getAndIncrement()
        )
        chain.apply()
        chainData.clear()
    }

    override fun close() {
        closeHandler.close()
    }

    private class ChainItem<A, S, E>(
        private val middleware: MviMiddleware<A, S, E>,
        private val chainData: ChainData<A, S, E>,
        private val next: ChainItem<A, S, E>?
    ) : MviMiddleware.Chain<A, S, E>, Closeable {

        override val action: A
            get() = chainData.action
        override val state: S
            get() = chainData.stateProvider.get()
        override val actionDispatcher: Dispatcher<A>
            get() = chainData.actionDispatcher
        override val eventDispatcher: Dispatcher<E>
            get() = chainData.eventDispatcher
        private var lastSequence: Long? = null

        override fun proceed() {
            next?.apply() ?: throw IllegalStateException("There is no next item in chain")
        }

        fun apply() {
            val sequence = chainData.sequence
            if (sequence != lastSequence) {
                lastSequence = sequence
                middleware.apply(this)
            } else throw IllegalStateException("MviMiddleware.apply() is called multiple times in a row")
        }

        override fun close() {
            middleware.close()
            next?.close()
        }
    }

    private class SettableChainData<A, S, E> :
        ChainData<A, S, E> {
        private var _action: A? = null
        private var _stateProvider: Provider<S>? = null
        private var _actionDispatcher: Dispatcher<A>? = null
        private var _eventDispatcher: Dispatcher<E>? = null
        private var _sequence: Long? = 0
        override val action: A
            get() = _action
                ?: throw IllegalStateException("Action is not set")
        override val stateProvider: Provider<S>
            get() = _stateProvider
                ?: throw IllegalStateException("State provider is not set")
        override val actionDispatcher: Dispatcher<A>
            get() = _actionDispatcher
                ?: throw IllegalStateException("Action dispatcher is not set")
        override val eventDispatcher: Dispatcher<E>
            get() = _eventDispatcher
                ?: throw IllegalStateException("Event dispatcher is not set")
        override val sequence: Long
            get() = _sequence
                ?: throw IllegalStateException("Sequence id is not set")

        fun set(
            action: A,
            stateProvider: Provider<S>,
            actionDispatcher: Dispatcher<A>,
            eventDispatcher: Dispatcher<E>,
            sequence: Long
        ) {
            _action = action
            _stateProvider = stateProvider
            _actionDispatcher = actionDispatcher
            _eventDispatcher = eventDispatcher
            _sequence = sequence
        }

        fun clear() {
            _action = null
            _stateProvider = null
            _actionDispatcher = null
            _eventDispatcher = null
            _sequence = null
        }
    }

    private interface ChainData<A, S, E> {
        val action: A
        val stateProvider: Provider<S>
        val actionDispatcher: Dispatcher<A>
        val eventDispatcher: Dispatcher<E>
        val sequence: Long
    }
}