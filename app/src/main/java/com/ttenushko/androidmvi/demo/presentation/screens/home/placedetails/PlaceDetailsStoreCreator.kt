package com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails

import android.os.Bundle
import com.ttenushko.androidmvi.*
import com.ttenushko.androidmvi.demo.App
import com.ttenushko.androidmvi.demo.domain.application.usecase.DeletePlaceUseCaseImpl
import com.ttenushko.androidmvi.demo.domain.weather.usecase.GetCurrentWeatherConditionsUseCaseImpl
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.*
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.PlaceDetailsStore.*
import com.ttenushko.androidmvi.demo.presentation.utils.MviLogger

internal class PlaceDetailsStoreCreator(private val placeId: Long) :
    MviStoreCreator<Intention, State, Event> {

    override fun createMviStore(savedState: Bundle?): MviStore<Intention, State, Event> =
        MviStores.create(
            initialState = State(placeId, null, null, false, false),
            bootstrapper = Bootstrapper(),
            middleware = listOf(
                LoggingMiddleware(MviLogger("mvi")),
                MviPostProcessorMiddleware(listOf(SideEffects())),
                GetCurrentWeatherMiddleware(GetCurrentWeatherConditionsUseCaseImpl(App.instance.weatherForecastRepository)),
                DeletePlaceMiddleware(DeletePlaceUseCaseImpl(App.instance.applicationSettings))
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

    override fun saveState(
        mviStore: MviStore<Intention, State, Event>,
        outState: Bundle
    ) {
        // do nothing
    }
}
