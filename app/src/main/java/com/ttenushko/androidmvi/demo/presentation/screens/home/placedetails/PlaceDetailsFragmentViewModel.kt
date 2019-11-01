package com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails

import android.os.Bundle
import com.ttenushko.androidmvi.*
import com.ttenushko.androidmvi.demo.domain.application.usecase.DeletePlaceUseCase
import com.ttenushko.androidmvi.demo.domain.weather.usecase.GetCurrentWeatherConditionsUseCase
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.*
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.PlaceDetailsStore.*
import com.ttenushko.androidmvi.demo.presentation.utils.MviLogger

internal class PlaceDetailsFragmentViewModel(
    private val mviLogger: MviLogger<Action, State>,
    private val placeId: Long,
    private val getCurrentWeatherConditionsUseCase: GetCurrentWeatherConditionsUseCase,
    private val deletePlaceUseCase: DeletePlaceUseCase
) : MviStoreViewModel<Intention, State, Event>() {

    override fun onCreateMviStore(savedState: Bundle?): MviStore<Intention, State, Event> =
        MviStores.create(
            initialState = State(placeId, null, null, false, false),
            bootstrapper = Bootstrapper(),
            middleware = listOf(
                LoggingMiddleware(mviLogger),
                MviPostProcessorMiddleware(listOf(SideEffects())),
                GetCurrentWeatherMiddleware(getCurrentWeatherConditionsUseCase),
                DeletePlaceMiddleware(deletePlaceUseCase)
            ),
            reducer = Reducer(),
            intentToActionTransformer = object : Transformer<Intention, Action> {
                override fun transform(input: Intention): Action =
                    when (input) {
                        is Intention.DeleteClicked -> Action.DeleteClicked
                        is Intention.DeleteConfirmed -> Action.Delete
                        is Intention.Refresh -> Action.Reload
                    }
            }
        )

    override fun onSaveState(mviStore: MviStore<Intention, State, Event>, outState: Bundle) {
        // do nothing
    }
}