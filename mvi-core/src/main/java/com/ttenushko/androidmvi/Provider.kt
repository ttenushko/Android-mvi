package com.ttenushko.androidmvi

interface Provider<T> {
    fun get(): T
}