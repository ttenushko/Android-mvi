package com.ttenushko.mvi

public fun interface MviBootstrapper<A, S, E> {
    public fun bootstrap(state: S, dispatchAction: Dispatcher<A>, dispatchEvent: Dispatcher<E>)
}