package com.ttenushko.mvi.android.compose

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.ttenushko.mvi.MviStore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

public abstract class MviWidget<I, S, E> : Widget {

    @Composable
    override fun Render() {
        val mviStore = rememberSaveable(
            saver = mapSaver(
                restore = { savedState ->
                    onCreateMviStore(savedState.ifEmpty { null })
                },
                save = {
                    onSaveMviStoreState(it.state) ?: emptyMap()
                }
            )
        ) {
            onCreateMviStore(null)
        }
        val state = remember(mviStore) { mutableStateOf(mviStore.state) }
        val events = remember(mviStore) { Channel<E>(Channel.UNLIMITED) }
        DisposableEffect(mviStore, state, events) {
            val stateChangedListener = MviStore.StateChangedListener {
                state.value = mviStore.state
            }
            val eventListener = MviStore.EventListener<E> { event ->
                events.trySend(event)
            }
            if (!mviStore.isRunning) {
                mviStore.run()
            }
            onDispose {
                mviStore.removeEventListener(eventListener)
                mviStore.removeStateChangedListener(stateChangedListener)
            }
        }
        Render(state, events, mviStore::dispatchIntent)
    }

    @Composable
    protected abstract fun Render(
        state: State<S>,
        events: ReceiveChannel<E>,
        dispatchIntent: (I) -> Unit,
    )

    protected abstract fun onCreateMviStore(savedState: Map<String, Any>?): MviStore<I, S, E>

    protected abstract fun onSaveMviStoreState(state: S): Map<String, Any>?
}