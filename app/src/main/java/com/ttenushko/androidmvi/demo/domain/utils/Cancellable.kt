package com.ttenushko.androidmvi.demo.domain.utils

interface Cancellable {
    val isCancelled: Boolean
    fun cancel()
}