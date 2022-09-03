package com.ttenushko.mvi

public fun interface MviReducer<A, S, E> {
    public fun reduce(action: A, State: S, dispatchEvent: Dispatcher<E>): S
}