package com.ttenushko.androidmvi

import java.io.Closeable

interface MviMiddleware<A, S, E> : Closeable {
    fun apply(chain: Chain<A, S, E>)

    interface Chain<A, S, E> {
        val action: A
        val stateProvider: Provider<S>
        val actionDispatcher: Dispatcher<A>
        val eventDispatcher: Dispatcher<E>

        fun proceed()
    }
}