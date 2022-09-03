package com.ttenushko.androidmvi.demo.presentation.screen.placedetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModelProvider
import com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.PlaceDetailsViewModel
import com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.mvi.Store
import com.ttenushko.androidmvi.demo.presentation.screen.placedetails.view.PlaceDetailsView
import com.ttenushko.mvi.android.compose.MviScreen
import kotlinx.coroutines.channels.ReceiveChannel

class PlaceDetailsScreen(
    viewModelFactory: ViewModelProvider.Factory,
) : MviScreen<Store.Intent, Store.State, Store.Event>(
    viewModelClass = PlaceDetailsViewModel::class.java,
    viewModelFactory = viewModelFactory
) {
    @Suppress("UNCHECKED_CAST")
    @Composable
    override fun Render(
        state: State<Store.State>,
        events: ReceiveChannel<Store.Event>,
        dispatchIntent: (Store.Intent) -> Unit,
    ) {
        PlaceDetailsView(
            state.value,
            events,
            dispatchIntent
        )
    }
}