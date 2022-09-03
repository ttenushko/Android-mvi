package com.ttenushko.mvi

import kotlinx.coroutines.CoroutineDispatcher
import java.io.Closeable

public interface MviStore<I, S, E> : Closeable {
    public val isRunning: Boolean
    public val isClosed: Boolean
    public val state: S

    public fun run()
    public fun dispatchIntent(intent: I)
    public fun addStateChangedListener(listener: StateChangedListener)
    public fun removeStateChangedListener(listener: StateChangedListener)
    public fun addEventListener(listener: EventListener<E>)
    public fun removeEventListener(listener: EventListener<E>)

    public fun interface StateChangedListener {
        public fun onStateChanged()
    }

    public fun interface EventListener<E> {
        public fun onEvent(event: E)
    }
}

public fun <I, A, S, E> createMviStore(
    initialState: S,
    intentToActionConverter: ((I) -> A?)? = null,
    bootstrapper: MviBootstrapper<A, S, E>? = null,
    middleware: Collection<MviMiddleware<A, S, E>>? = null,
    reducer: MviReducer<A, S, E>,
    coroutineDispatcher: CoroutineDispatcher
): MviStore<I, S, E> =
    MviStoreImpl(
        initialState,
        intentToActionConverter,
        bootstrapper,
        middleware,
        reducer,
        coroutineDispatcher
    )