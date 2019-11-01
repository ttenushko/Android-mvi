package com.ttenushko.androidmvi.demo.presentation.screens.home.places

import android.os.Bundle
import com.ttenushko.androidmvi.*
import com.ttenushko.androidmvi.demo.domain.application.usecase.TrackSavedPlacesUseCase
import com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi.*
import com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi.PlacesStore.*
import com.ttenushko.androidmvi.demo.presentation.utils.MviLogger

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
            intentToActionTransformer = object : Transformer<Intention, Action> {
                override fun transform(input: Intention): Action =
                    when (input) {
                        is Intention.AddPlaceButtonClicked -> Action.AddPlaceButtonClicked
                        is Intention.PlaceClicked -> Action.PlaceClicked(input.place)
                    }
            }
        )

    override fun onSaveState(mviStore: MviStore<Intention, State, Event>, outState: Bundle) {
        // do nothing
    }
}