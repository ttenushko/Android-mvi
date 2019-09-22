package com.ttenushko.androidmvi

import java.io.Closeable

interface MviStore<I, S, E> : Runnable, Closeable {
    val state: S

    fun dispatchIntent(intent: I)
    fun addStateChangedListener(listener: StateChangedListener<S>)
    fun removeStateChangedListener(listener: StateChangedListener<S>)
    fun addEventListener(listener: EventListener<E>)
    fun removeEventListener(listener: EventListener<E>)

    interface StateChangedListener<S> {
        fun onStateChanged(state: S)
    }

    interface EventListener<E> {
        fun onEvent(event: E)
    }
}