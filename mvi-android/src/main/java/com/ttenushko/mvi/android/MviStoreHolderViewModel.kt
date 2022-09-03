package com.ttenushko.mvi.android

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ttenushko.mvi.MviStore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

public abstract class MviStoreHolderViewModel<I, S, E>(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    public val mviStore: MviStore<I, S, E>
        get() {
            return _mviStore
        }
    public val state: StateFlow<S>
        get() {
            return _state
        }
    public val events: ReceiveChannel<E>
        get() {
            return _events
        }
    private val _mviStore: MviStore<I, S, E> by lazy {
        createMviStore(savedStateHandle.get<Bundle?>(SAVED_MVI_STORE_STATE)
            ?.let { if (!it.isEmpty) it else null }).also { mviStore ->
            mviStore.addStateChangedListener { _state.value = _mviStore.state }
            mviStore.addEventListener { e -> _events.trySend(e) }
        }
    }
    private val _state: MutableStateFlow<S> by lazy { MutableStateFlow(_mviStore.state) }
    private val _events = Channel<E>(capacity = Channel.UNLIMITED)

    init {
        savedStateHandle.setSavedStateProvider(SAVED_MVI_STORE_STATE) {
            saveMviStoreState(_mviStore.state) ?: Bundle()
        }
    }

    override fun onCleared() {
        super.onCleared()
        _mviStore.close()
    }

    protected abstract fun createMviStore(savedState: Bundle?): MviStore<I, S, E>

    protected abstract fun saveMviStoreState(state: S): Bundle?

    private companion object {
        private const val SAVED_MVI_STORE_STATE = "savedMviStoreState"
    }
}