package com.ttenushko.androidmvi.demo.presentation.utils

class ValueUpdater<T : Any>(
    initialValue: T,
    private val updater: (T) -> Unit
) {
    private var value = initialValue

    fun set(value: T) {
        if (this.value != value) {
            this.value = value
            updater(value)
        }
    }

    fun forceUpdate() {
        updater(this.value)
    }
}