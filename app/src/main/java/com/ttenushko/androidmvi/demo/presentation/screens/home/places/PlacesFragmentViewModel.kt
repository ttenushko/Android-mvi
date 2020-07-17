package com.ttenushko.androidmvi.demo.presentation.screens.home.places

import android.os.Bundle
import com.ttenushko.androidmvi.demo.domain.application.usecase.TrackSavedPlacesUseCase
import com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi.*
import com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi.PlacesStore.*
import com.ttenushko.androidmvi.demo.presentation.utils.MviLogger
import com.ttenushko.mvi.*
import com.ttenushko.mvi.android.MviStoreViewModel

internal class PlacesFragmentViewModel(
    private val mviLogger: MviLogger<Action, State>,
    private val trackSavedPlacesUseCase: TrackSavedPlacesUseCase
) : MviStoreViewModel<Intention, State, Event>() {

    override fun onCreateMviStore(savedState: Bundle?): MviStore<Intention, State, Event> =
        MviStores.create(
            initialState = State(null, null, false),
            bootstrapper = Bootstrapper(),
            middleware = listOf(
                LoggingMiddleware(mviLogger),
                MviPostProcessorMiddleware(listOf(SideEffects())),
                TrackSavedPlacesMiddleware(trackSavedPlacesUseCase)
            ),
            reducer = Reducer(),
            intentToActionIntentToActionConverter = object :
                IntentToActionConverter<Intention, Action> {
                override fun convert(intent: Intention): Action =
                    when (intent) {
                        is Intention.AddPlaceButtonClicked -> Action.AddPlaceButtonClicked
                        is Intention.PlaceClicked -> Action.PlaceClicked(intent.place)
                    }
            }
        )

    override fun onSaveState(mviStore: MviStore<Intention, State, Event>, outState: Bundle) {
        // do nothing
    }
}