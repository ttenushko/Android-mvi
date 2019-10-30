package com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi

import com.ttenushko.androidmvi.MviMiddleware
import com.ttenushko.androidmvi.MviMiddlewareImpl
import com.ttenushko.androidmvi.demo.domain.application.usecase.TrackSavedPlacesUseCase
import com.ttenushko.androidmvi.demo.domain.utils.Either
import com.ttenushko.androidmvi.demo.presentation.utils.task.MultiResultTask
import com.ttenushko.androidmvi.demo.presentation.utils.task.asMultiResultJob
import kotlinx.coroutines.Dispatchers

internal class TrackSavedPlacesMiddleware(
    private val trackSavedPlacesUseCase: TrackSavedPlacesUseCase
) : MviMiddlewareImpl<Action, PlacesStore.State, PlacesStore.Event>() {

    private val taskTrackSavedPlaces = createTrackSavedPlacesTask()

    override fun apply(chain: MviMiddleware.Chain<Action, PlacesStore.State, PlacesStore.Event>) {
        super.apply(chain)
        when (chain.action) {
            is Action.Initialize -> {
                if (!taskTrackSavedPlaces.isRunning) {
                    taskTrackSavedPlaces.start(TrackSavedPlacesUseCase.Param(), Unit)
                }
                chain.proceed()
            }
            else -> chain.proceed()
        }
    }

    override fun onClose() {
        taskTrackSavedPlaces.stop()
    }

    private fun createTrackSavedPlacesTask() =
        MultiResultTask<TrackSavedPlacesUseCase.Param, TrackSavedPlacesUseCase.Result, Unit>(
            "trackSavedPlaces",
            Dispatchers.Main,
            { param, tag ->
                actionDispatcher.dispatch(Action.LoadingSavedPlaces)
                trackSavedPlacesUseCase.asMultiResultJob(param, tag)
            },
            { result, _ ->
                actionDispatcher.dispatch(Action.SavedPlacesUpdated(Either.Right(result.places)))
            },
            { error, _ ->
                actionDispatcher.dispatch(Action.SavedPlacesUpdated(Either.Left(error)))
            }
        )
}