package com.ttenushko.androidmvi

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.atomic.AtomicInteger


class MviStoreViewModel<I, S, E>(
    private val mviStoreCreator: MviStoreCreator<I, S, E>,
    savedState: Bundle?
) : ViewModel(), Runnable {

    companion object {
        private const val STATUS_UNINITIALIZED = 0
        private const val STATUS_RUNNING = 1
        private const val STATUS_CLOSED = 2
    }

    private val status = AtomicInteger(STATUS_UNINITIALIZED)
    private val stateLiveData = MutableLiveData<S>()
    private val eventsLiveData = MutableLiveDataQueue<E>()
    private val mviStore: MviStore<I, S, E> = mviStoreCreator.createMviStore(savedState)
    private val stateChangedListener = object : MviStore.StateChangedListener<S> {
        override fun onStateChanged(state: S) {
            stateLiveData.value = state
        }
    }
    private val eventListener = object : MviStore.EventListener<E> {
        override fun onEvent(event: E) {
            eventsLiveData.setValue(event)
        }
    }
    val state: LiveData<S>
        get() = stateLiveData
    val events: LiveData<E>
        get() = eventsLiveData

    override fun run() {
        if (status.compareAndSet(STATUS_UNINITIALIZED, STATUS_RUNNING)) {
            mviStore.addStateChangedListener(stateChangedListener)
            mviStore.addEventListener(eventListener)
            stateLiveData.value = mviStore.state
            mviStore.run()
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (STATUS_RUNNING == status.getAndSet(STATUS_CLOSED)) {
            mviStore.removeStateChangedListener(stateChangedListener)
            mviStore.removeEventListener(eventListener)
            mviStore.close()
        }
    }

    fun dispatchIntent(intent: I) {
        checkRunning()
        mviStore.dispatchIntent(intent)
    }

    fun saveState(outState: Bundle) {
        checkRunning()
        mviStoreCreator.saveState(mviStore, outState)
    }

    private fun checkRunning() {
        when (status.get()) {
            STATUS_UNINITIALIZED -> throw IllegalStateException("This instance is not running yet.")
            STATUS_CLOSED -> throw IllegalStateException("This instance is already closed.")
        }
    }
}