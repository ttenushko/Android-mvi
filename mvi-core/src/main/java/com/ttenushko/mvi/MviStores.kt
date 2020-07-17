package com.ttenushko.mvi

object MviStores {
    fun <I, A, S, E> create(
        initialState: S,
        intentToActionIntentToActionConverter: IntentToActionConverter<I, A>? = null,
        bootstrapper: MviBootstrapper<A, S, E>? = null,
        middleware: Collection<MviMiddleware<A, S, E>>? = null,
        reducer: MviReducer<A, S>
    ): MviStore<I, S, E> =
        MviStoreImpl(
            initialState,
            intentToActionIntentToActionConverter,
            bootstrapper,
            middleware,
            reducer
        )
}