package com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi

import com.ttenushko.androidmvi.demo.domain.application.usecase.SavePlaceUseCase
import com.ttenushko.androidmvi.demo.domain.utils.Either
import com.ttenushko.androidmvi.demo.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.AddPlaceStore.Event
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.AddPlaceStore.State
import com.ttenushko.androidmvi.demo.presentation.utils.usecase.createExecutor
import com.ttenushko.androidmvi.demo.presentation.utils.usecase.singleResultUseCaseProvider
import com.ttenushko.mvi.MviMiddleware
import com.ttenushko.mvi.MviMiddlewareImpl

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
        createExecutor<SavePlaceUseCase.Param, SavePlaceUseCase.Result, Unit>(
            singleResultUseCaseProvider { _, _ -> savePlaceUseCase },
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