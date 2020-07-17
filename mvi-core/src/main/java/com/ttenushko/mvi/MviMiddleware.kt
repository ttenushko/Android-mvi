package com.ttenushko.mvi

import java.io.Closeable

interface MviMiddleware<A, S, E> : Closeable {
    fun apply(chain: Chain<A, S, E>)

    interface Chain<A, S, E> {
        val action: A
        val state: S
        val actionDispatcher: Dispatcher<A>
        val eventDispatcher: Dispatcher<E>

        fun proceed()
    }
}