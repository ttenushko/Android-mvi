package com.ttenushko.androidmvi.demo.presentation.screens.home.places

import android.os.Bundle
import com.ttenushko.androidmvi.*
import com.ttenushko.androidmvi.demo.App
import com.ttenushko.androidmvi.demo.domain.application.usecase.TrackSavedPlacesUseCaseImpl
import com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi.*
import com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi.PlacesStore.*
import com.ttenushko.androidmvi.demo.presentation.utils.MviLogger

internal class PlacesFragmentStoreCreator :
    MviStoreCreator<Intention, State, Event> {

    override fun createMviStore(savedState: Bundle?): MviStore<Intention, State, Event> =
        MviStores.create(
            initialState = State(null, null, false),
            bootstrapper = Bootstrapper(),
            middleware = listOf(
                LoggingMiddleware(MviLogger("mvi")),
                MviPostProcessorMiddleware(listOf(SideEffects())),
                TrackSavedPlacesMiddleware(TrackSavedPlacesUseCaseImpl(App.instance.applicationSettings))
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

    override fun saveState(
        mviStore: MviStore<Intention, State, Event>,
        outState: Bundle
    ) {
        // do nothing
    }
}
