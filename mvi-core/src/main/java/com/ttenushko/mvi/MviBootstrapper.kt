package com.ttenushko.mvi


interface MviBootstrapper<A, S, E> {
    fun bootstrap(state: S, actionDispatcher: Dispatcher<A>, eventDispatcher: Dispatcher<E>)
}
