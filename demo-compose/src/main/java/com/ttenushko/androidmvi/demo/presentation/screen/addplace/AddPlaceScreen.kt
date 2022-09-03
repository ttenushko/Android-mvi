package com.ttenushko.androidmvi.demo.presentation.screen.addplace

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModelProvider
import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.AddPlaceViewModel
import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi.Store
import com.ttenushko.mvi.android.compose.MviScreen
import com.ttenushko.androidmvi.demo.presentation.screen.addplace.view.AddPlaceView
import kotlinx.coroutines.channels.ReceiveChannel

class AddPlaceScreen(
    viewModelFactory: ViewModelProvider.Factory,
) : MviScreen<Store.Intent, Store.State, Store.Event>(
    viewModelClass = AddPlaceViewModel::class.java,
    viewModelFactory = viewModelFactory
) {

    @Composable
    override fun Render(
        state: State<Store.State>,
        events: ReceiveChannel<Store.Event>,
        dispatchIntent: (Store.Intent) -> Unit,
    ) {
        AddPlaceView(state.value, events, dispatchIntent)
    }
}