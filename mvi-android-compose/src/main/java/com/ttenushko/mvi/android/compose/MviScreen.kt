package com.ttenushko.mvi.android.compose

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ttenushko.mvi.android.MviStoreHolderViewModel
import kotlinx.coroutines.channels.ReceiveChannel

public abstract class MviScreen<I, S, E>(
    private val viewModelClass: Class<out MviStoreHolderViewModel<I, S, E>>,
    private val viewModelFactory: ViewModelProvider.Factory? = null,
    private val viewModelKey: String? = null,
) : Screen {

    @Composable
    override fun Render() {
        val viewModel = viewModel(
            modelClass = viewModelClass,
            factory = viewModelFactory,
            key = viewModelKey
        )
        val lifecycle by rememberUpdatedState(LocalLifecycleOwner.current.lifecycle)
        DisposableEffect(viewModel.mviStore, viewModel.state, viewModel.events, lifecycle) {
            val lifecycleObserver = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_CREATE -> {
                        onActive(viewModel.mviStore::dispatchIntent)
                    }
                    Lifecycle.Event.ON_START -> {
                        onVisible(viewModel.mviStore::dispatchIntent)
                    }
                    Lifecycle.Event.ON_STOP -> {
                        onInvisible(viewModel.mviStore::dispatchIntent)
                    }
                    Lifecycle.Event.ON_DESTROY -> {
                        onInactive(viewModel.mviStore::dispatchIntent)
                    }
                    else -> {
                        // ignore
                    }
                }
            }
            if (!viewModel.mviStore.isRunning) {
                viewModel.mviStore.run()
            }
            lifecycle.addObserver(lifecycleObserver)

            onDispose {
                lifecycle.removeObserver(lifecycleObserver)
            }
        }
        Render(
            viewModel.state.collectAsState(),
            viewModel.events,
            viewModel.mviStore::dispatchIntent
        )
    }

    @Composable
    protected abstract fun Render(
        state: State<S>,
        events: ReceiveChannel<E>,
        dispatchIntent: (I) -> Unit,
    )

    protected open fun onActive(dispatchIntent: (I) -> Unit) {}

    protected open fun onInactive(dispatchIntent: (I) -> Unit) {}

    protected open fun onVisible(dispatchIntent: (I) -> Unit) {}

    protected open fun onInvisible(dispatchIntent: (I) -> Unit) {}
}