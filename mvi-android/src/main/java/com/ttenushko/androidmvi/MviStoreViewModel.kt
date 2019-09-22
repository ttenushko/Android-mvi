package com.ttenushko.androidmvi

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.atomic.AtomicInteger


@Suppress("LeakingThis")
abstract class MviStoreViewModel<I, S, E>(private var savedState: Bundle?) : ViewModel(), Runnable {

    companion object {
        private const val STATUS_UNINITIALIZED = 0
        private const val STATUS_RUNNING = 1
        private const val STATUS_CLOSED = 2
    }

    private val status = AtomicInteger(STATUS_UNINITIALIZED)
    private val stateLiveData = MutableLiveData<S>()
    private val eventsLiveData = MutableLiveDataQueue<E>()
    private lateinit var mviStore: MviStore<I, S, E>
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

    final override fun run() {
        if (status.compareAndSet(STATUS_UNINITIALIZED, STATUS_RUNNING)) {
            mviStore = createMviStore(savedState).also { savedState = null }
            mviStore.addStateChangedListener(stateChangedListener)
            mviStore.addEventListener(eventListener)
            stateLiveData.value = mviStore.state
            mviStore.run()
        }
    }

    final override fun onCleared() {
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
        saveState(mviStore.state, outState)
    }

    abstract fun createMviStore(savedState: Bundle?): MviStore<I, S, E>

    open fun saveState(state: S, outState: Bundle) {

    }

    private fun checkRunning() {
        when (status.get()) {
            STATUS_UNINITIALIZED -> throw IllegalStateException("This instance is not running yet.")
            STATUS_CLOSED -> throw IllegalStateException("This instance is already closed.")
        }
    }
}