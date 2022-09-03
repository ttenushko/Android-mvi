package com.ttenushko.mvi

import java.io.Closeable
import java.util.concurrent.atomic.AtomicBoolean


internal class CloseHandler(val closeHandler: () -> Unit) : Closeable {

    val isClosed: Boolean
        get() = _isClosed.get()
    private val _isClosed = AtomicBoolean(false)

    fun checkNotClosed() {
        if (_isClosed.get()) {
            throw IllegalStateException("This instance is closed")
        }
    }

    override fun close() {
        if (!_isClosed.getAndSet(true)) {
            closeHandler()
        }
    }
}