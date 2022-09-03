package com.ttenushko.mvi

import java.io.Closeable

public interface MviMiddleware<A, S, E> : Closeable {
    public fun setup(
        provideState: Provider<S>,
        dispatchAction: Dispatcher<A>,
        dispatchEvent: Dispatcher<E>
    )

    public fun apply(action: A, chain: Chain)

    public interface Chain {
        public fun proceed()
    }
}