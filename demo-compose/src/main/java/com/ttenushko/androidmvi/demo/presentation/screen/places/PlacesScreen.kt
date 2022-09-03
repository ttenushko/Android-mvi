package com.ttenushko.androidmvi.demo.presentation.screen.places

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModelProvider
import com.ttenushko.androidmvi.demo.common.presentation.screen.places.PlacesViewModel
import com.ttenushko.androidmvi.demo.common.presentation.screen.places.mvi.Store
import com.ttenushko.mvi.android.compose.MviScreen
import com.ttenushko.androidmvi.demo.presentation.screen.places.view.PlacesView
import kotlinx.coroutines.channels.ReceiveChannel

class PlacesScreen(
    viewModelFactory: ViewModelProvider.Factory
) : MviScreen<Store.Intent, Store.State, Store.Event>(
    viewModelClass = PlacesViewModel::class.java,
    viewModelFactory = viewModelFactory
) {
    @Composable
    override fun Render(
        state: State<Store.State>,
        events: ReceiveChannel<Store.Event>,
        dispatchIntent: (Store.Intent) -> Unit,
    ) {
        PlacesView(state.value, events, dispatchIntent)
    }
}