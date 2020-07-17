package com.ttenushko.mvi.android

import androidx.annotation.MainThread
import androidx.lifecycle.*
import java.util.concurrent.atomic.AtomicBoolean


internal open class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val liveDataToObserve: LiveData<T>
    private val mPending = AtomicBoolean(false)

    init {
        val outputLiveData = MediatorLiveData<T>()
        outputLiveData.addSource(this) { currentValue ->
            outputLiveData.value = currentValue
            mPending.set(false)
        }
        liveDataToObserve = outputLiveData
    }

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        liveDataToObserve.observe(owner, Observer<T> { t ->
            if (mPending.get()) {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(value: T) {
        mPending.set(true)
        super.setValue(value)
    }
}