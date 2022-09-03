package com.ttenushko.androidmvi.demo.common.domain.utils

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.concurrent.CopyOnWriteArraySet

interface ObservableValue<T> {
    val value: T

    fun addObserver(observer: ValueObserver)
    fun removeObserver(observer: ValueObserver)

    fun interface ValueObserver {
        fun onChanged()
    }
}

class ImmutableObservableValue<T>(
    staticValue: T
) : ObservableValue<T> {

    override val value: T = staticValue

    override fun addObserver(observer: ObservableValue.ValueObserver) {
        // do nothing
    }

    override fun removeObserver(observer: ObservableValue.ValueObserver) {
        // do nothing
    }
}

class MutableObservableValue<T>(
    defaultValue: T,
    private val notifyOnSameValueSet: Boolean = false
) : ObservableValue<T> {

    private val lock = Any()
    private val observers = CopyOnWriteArraySet<ObservableValue.ValueObserver>()

    @Volatile
    override var value: T = defaultValue
        set(value) {
            synchronized(lock) {
                if (field != value || notifyOnSameValueSet) {
                    field = value
                } else null
            }?.also {
                observers.forEach { it.onChanged() }
            }
        }

    override fun addObserver(observer: ObservableValue.ValueObserver) {
        observers.add(observer)
    }

    override fun removeObserver(observer: ObservableValue.ValueObserver) {
        observers.remove(observer)
    }
}

@Suppress("EXPERIMENTAL_API_USAGE")
fun <T> ObservableValue<T>.trackChanged(): Flow<Unit> =
    callbackFlow {
        val observer = ObservableValue.ValueObserver {
            trySend(Unit)
        }
        addObserver(observer)
        awaitClose { removeObserver(observer) }
    }

fun <T> ObservableValue<T>.asFlow(): Flow<T> =
    trackChanged()
        .onStart { emit(Unit) }
        .map { this@asFlow.value }