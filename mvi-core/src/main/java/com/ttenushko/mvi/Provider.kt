package com.ttenushko.mvi

internal interface Provider<T> {
    fun get(): T
}