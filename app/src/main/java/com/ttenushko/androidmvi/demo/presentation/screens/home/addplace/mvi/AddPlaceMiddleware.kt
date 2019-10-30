package com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi

import com.ttenushko.androidmvi.MviMiddleware
import com.ttenushko.androidmvi.MviMiddlewareImpl
import com.ttenushko.androidmvi.demo.domain.application.usecase.SavePlaceUseCase
import com.ttenushko.androidmvi.demo.domain.utils.Either
import com.ttenushko.androidmvi.demo.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.AddPlaceStore.Event
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.AddPlaceStore.State
import com.ttenushko.androidmvi.demo.presentation.utils.task.SingleResultTask
import com.ttenushko.androidmvi.demo.presentation.utils.task.asSingleResultJob
import kotlinx.coroutines.Dispatchers

internal class AddPlaceMiddleware(
    private val savePlaceUseCase: SavePlaceUseCase
) : MviMiddlewareImpl<Action, State, Event>() {

    private val taskAddPlace = createSavePlaceTask()

    override fun apply(chain: MviMiddleware.Chain<Action, State, Event>) {
        super.apply(chain)
        when (val action = chain.action) {
            is Action.PlaceClicked -> {
                if (!taskAddPlace.isRunning) {
                    taskAddPlace.start(SavePlaceUseCase.Param(action.place), Unit)
                }
            }
            else -> chain.proceed()
        }
    }

    override fun onClose() {
        taskAddPlace.stop()
    }

    private fun createSavePlaceTask() =
        SingleResultTask<SavePlaceUseCase.Param, SavePlaceUseCase.Result, Unit>(
            "savePlace",
            Dispatchers.Main,
            { param, tag ->
                savePlaceUseCase.asSingleResultJob(param, tag)
            },
            { result, _ ->
                actionDispatcher.dispatch(
                    Action.PlaceSaved(
                        if (result.isAdded) Either.Right(result.place)
                        else Either.Left<Throwable, Place>(IllegalStateException())
                    )
                )
            },
            { error, _ ->
                actionDispatcher.dispatch(
                    Action.PlaceSaved(Either.Left(error))
                )
            }
        )
}