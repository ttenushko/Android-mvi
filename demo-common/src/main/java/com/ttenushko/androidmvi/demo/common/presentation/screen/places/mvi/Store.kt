package com.ttenushko.androidmvi.demo.common.presentation.screen.places.mvi

import com.ttenushko.androidmvi.demo.common.domain.application.usecase.TrackSavedPlacesUseCase
import com.ttenushko.androidmvi.demo.common.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.common.presentation.base.router.Router
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.common.presentation.screen.places.mvi.Store.*
import com.ttenushko.androidmvi.demo.common.presentation.utils.MviLogger
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.TaskExecutorFactory
import com.ttenushko.mvi.MviStore
import com.ttenushko.mvi.createMviStore
import com.ttenushko.mvi.extra.mviLoggingMiddleware
import kotlinx.coroutines.CoroutineDispatcher

interface Store : MviStore<Intent, State, Event> {

    data class State(
        val places: List<Place>?,
        val error: Throwable?,
    ) {
        val isLoading: Boolean
            get() {
                return null == error && null == places
            }
    }

    sealed class Intent {
        object AddPlaceButtonClicked : Intent()
        data class PlaceClicked(val place: Place) : Intent()
    }

    object Event

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun create(
            initialState: State,
            trackSavedPlacesUseCase: TrackSavedPlacesUseCase,
            taskExecutorFactory: TaskExecutorFactory,
            mviLogger: MviLogger<*, *>,
            router: Router<MainRouter.Destination>,
            coroutineDispatcher: CoroutineDispatcher,
        ): MviStore<Intent, State, Event> =
            createMviStore(
                initialState = initialState,
                bootstrapper = bootstrapper(),
                middleware = listOf(
                    mviLoggingMiddleware(mviLogger as MviLogger<Action, State>),
                    trackSavedPlacesMiddleware(trackSavedPlacesUseCase, taskExecutorFactory)
                ),
                reducer = reducer(router),
                intentToActionConverter = { intent -> Action.Intent(intent) },
                coroutineDispatcher = coroutineDispatcher
            )
    }
}