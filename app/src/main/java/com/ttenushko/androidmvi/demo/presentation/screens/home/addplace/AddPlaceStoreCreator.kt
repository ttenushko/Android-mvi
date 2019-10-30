package com.ttenushko.androidmvi.demo.presentation.screens.home.addplace

import android.os.Bundle
import com.ttenushko.androidmvi.*
import com.ttenushko.androidmvi.demo.App
import com.ttenushko.androidmvi.demo.domain.application.usecase.SavePlaceUseCaseImpl
import com.ttenushko.androidmvi.demo.domain.weather.usecase.SearchPlaceUseCaseImpl
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.*
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.AddPlaceStore.*
import com.ttenushko.androidmvi.demo.presentation.utils.MviLogger

internal class AddPlaceStoreCreator(private val search: String) :
    MviStoreCreator<Intention, State, Event> {

    companion object {
        private const val SEARCH = "search"
    }

    override fun createMviStore(savedState: Bundle?): MviStore<Intention, State, Event> {
        val search = savedState?.getString(SEARCH) ?: search
        return MviStores.create(
            initialState = State(search, State.SearchResult.Success("", listOf()), false),
            bootstrapper = Bootstrapper(),
            middleware = listOf(
                LoggingMiddleware(MviLogger("mvi")),
                MviPostProcessorMiddleware(listOf(SideEffects())),
                SearchPlaceMiddleware(
                    SearchPlaceUseCaseImpl(
                        App.instance.weatherForecastRepository,
                        300
                    )
                ),
                AddPlaceMiddleware(SavePlaceUseCaseImpl(App.instance.applicationSettings))
            ),
            reducer = Reducer(),
            intentToActionTransformer = object : Transformer<Intention, Action> {
                override fun transform(input: Intention): Action =
                    when (input) {
                        is Intention.SearchChanged -> Action.SearchChanged(input.search)
                        is Intention.PlaceClicked -> Action.PlaceClicked(input.place)
                    }
            }
        )
    }

    override fun saveState(
        mviStore: MviStore<Intention, State, Event>,
        outState: Bundle
    ) {
        outState.putString(SEARCH, mviStore.state.search)
    }
}