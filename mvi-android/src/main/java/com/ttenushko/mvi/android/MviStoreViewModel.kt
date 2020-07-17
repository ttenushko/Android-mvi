package com.ttenushko.mvi.android

import android.os.Bundle
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ttenushko.mvi.MviStore


abstract class MviStoreViewModel<I, S, E>(
    expectMviOperationsOnMainThread: Boolean = false
) : ViewModel() {

    companion object {
        private const val STATUS_IDLE = 0
        private const val STATUS_RUNNING = 1
        private const val STATUS_CLEARED = 2
    }

    private var status = STATUS_IDLE
    private val stateLiveData = MutableLiveData<S>()
    private val eventsLiveData = MutableLiveDataQueue<E>()
    private lateinit var mviStore: MviStore<I, S, E>
    private val stateChangedListener = if (expectMviOperationsOnMainThread) {
        object : MviStore.StateChangedListener<S> {
            override fun onStateChanged(state: S) {
                assertMainThread()
                stateLiveData.value = state
            }
        }
    } else {
        object : MviStore.StateChangedListener<S> {
            override fun onStateChanged(state: S) {
                stateLiveData.postValue(state)
            }
        }
    }
    private val eventListener = if (expectMviOperationsOnMainThread) {
        object : MviStore.EventListener<E> {
            override fun onEvent(event: E) {
                assertMainThread()
                eventsLiveData.setValue(event)
            }
        }
    } else {
        object : MviStore.EventListener<E> {
            override fun onEvent(event: E) {
                eventsLiveData.postValue(event)
            }
        }
    }

    val state: LiveData<S>
        get() = stateLiveData
    val events: LiveData<E>
        get() = eventsLiveData

    fun run(savedState: Bundle?) {
        assertMainThread()
        when (status) {
            STATUS_IDLE -> {
                mviStore = onCreateMviStore(savedState)
                mviStore.addStateChangedListener(stateChangedListener)
                mviStore.addEventListener(eventListener)
                stateLiveData.value = mviStore.state
                status = STATUS_RUNNING
                mviStore.run()
            }
            STATUS_RUNNING -> {
                // ignore multiple run() calls
            }
            STATUS_CLEARED -> instanceCleared()
        }
    }

    override fun onCleared() {
        super.onCleared()
        assertMainThread()
        when (status) {
            STATUS_IDLE -> {
                status = STATUS_CLEARED
            }
            STATUS_RUNNING -> {
                mviStore.removeStateChangedListener(stateChangedListener)
                mviStore.removeEventListener(eventListener)
                mviStore.close()
                status = STATUS_CLEARED
            }
            STATUS_CLEARED -> instanceCleared()
        }
    }

    fun dispatchIntent(intent: I) {
        assertMainThread()
        when (status) {
            STATUS_IDLE -> notRunning()
            STATUS_RUNNING -> mviStore.dispatchIntent(intent)
            STATUS_CLEARED -> instanceCleared()
        }
    }

    fun saveState(outState: Bundle) {
        assertMainThread()
        when (status) {
            STATUS_IDLE -> notRunning()
            STATUS_RUNNING -> onSaveState(mviStore, outState)
            STATUS_CLEARED -> instanceCleared()
        }
    }

    abstract fun onCreateMviStore(savedState: Bundle?): MviStore<I, S, E>

    abstract fun onSaveState(mviStore: MviStore<I, S, E>, outState: Bundle)

    private fun assertMainThread() {
        if (Looper.myLooper() !== Looper.getMainLooper()) {
            throw IllegalStateException("This method should be called on main thread.")
        }
    }

    private fun notRunning(): Nothing =
        throw IllegalStateException("This instance is not running yet. Call 'run()' first.")

    private fun instanceCleared(): Nothing =
        throw IllegalStateException("This instance is cleared.")
}