package com.ttenushko.androidmvi

interface MviReducer<A, S> {
    fun reduce(action: A, state: S): S
}
