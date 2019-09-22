package com.ttenushko.androidmvi


interface Dispatcher<T> {
    fun dispatch(value: T)
}