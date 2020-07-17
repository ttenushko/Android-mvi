package com.ttenushko.mvi

interface Dispatcher<T> {
    fun dispatch(value: T)
}