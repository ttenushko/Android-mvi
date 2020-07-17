package com.ttenushko.mvi

interface MviReducer<A, S> {
    fun reduce(action: A, state: S): S
}
