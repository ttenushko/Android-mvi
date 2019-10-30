package com.ttenushko.androidmvi.demo.domain.utils

import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import java.util.concurrent.CopyOnWriteArraySet

class ObservableValue<T>(defaultValue: T) {

    private val observers = CopyOnWriteArraySet<ValueObserver<T>>()
    private var value: T = defaultValue

    fun get(): T =
        value

    fun set(value: T) {
        if (this.value != value) {
            this.value = value
            observers.forEach { it.onChanged(value) }
        }
    }

    fun addObserver(observer: ValueObserver<T>, notifyImmediately: Boolean = false) {
        if (observers.add(observer) && notifyImmediately) {
            observer.onChanged(value)
        }
    }

    fun removeObserver(observer: ValueObserver<T>) {
        observers.remove(observer)
    }

    interface ValueObserver<T> {
        fun onChanged(value: T)
    }
}

fun <T> ObservableValue<T>.asRxObservable(): Observable<T> =
    Observable.create { emitter ->
        val valueObserver = object : ObservableValue.ValueObserver<T> {
            override fun onChanged(value: T) {
                if (!emitter.isDisposed) {
                    emitter.onNext(value)
                }
            }
        }
        if (!emitter.isDisposed) {
            emitter.setDisposable(Disposables.fromAction {
                this@asRxObservable.removeObserver(valueObserver)
            })
            this@asRxObservable.addObserver(valueObserver, true)
        }
    }