package com.ttenushko.androidmvi

import java.util.*

/**
 * This is special LiveData implementation that consumes all the values in inactive state and once becomes active,
 * sends remembered values to a subscriber(s)
 */
internal class MutableLiveDataQueue<T> : SingleLiveEvent<T>() {

    private val values: Queue<T> = LinkedList()

    private var isActive: Boolean = false

    override fun onActive() {
        isActive = true
        while (values.isNotEmpty()) {
            setValue(values.poll())
        }
    }

    override fun onInactive() {
        isActive = false
    }

    override fun setValue(value: T) {
        if (isActive) {
            super.setValue(value)
        } else {
            values.add(value)
        }
    }
}