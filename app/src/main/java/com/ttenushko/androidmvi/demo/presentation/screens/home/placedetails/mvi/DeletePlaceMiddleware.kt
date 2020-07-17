package com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi

import com.ttenushko.androidmvi.demo.domain.application.usecase.DeletePlaceUseCase
import com.ttenushko.androidmvi.demo.domain.utils.Either
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.PlaceDetailsStore.Event
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.PlaceDetailsStore.State
import com.ttenushko.androidmvi.demo.presentation.utils.usecase.createExecutor
import com.ttenushko.androidmvi.demo.presentation.utils.usecase.singleResultUseCaseProvider
import com.ttenushko.mvi.MviMiddleware
import com.ttenushko.mvi.MviMiddlewareImpl

internal class DeletePlaceMiddleware(
    private val deletePlaceUseCase: DeletePlaceUseCase
) : MviMiddlewareImpl<Action, State, Event>() {

    private val taskDeletePlace = createDeletePlaceTask()

    override fun apply(chain: MviMiddleware.Chain<Action, State, Event>) {
        super.apply(chain)
        when (chain.action) {
            is Action.Delete -> {
                if (!taskDeletePlace.isRunning) {
                    taskDeletePlace.start(
                        DeletePlaceUseCase.Param(chain.state.placeId),
                        Unit
                    )
                }
            }
            else -> chain.proceed()
        }
    }

    override fun onClose() {
        taskDeletePlace.stop()
    }

    private fun createDeletePlaceTask() =
        createExecutor<DeletePlaceUseCase.Param, DeletePlaceUseCase.Result, Unit>(
            singleResultUseCaseProvider { _, _ ->
                actionDispatcher.dispatch(Action.Deleting)
                deletePlaceUseCase
            },
            { result, _ ->
                actionDispatcher.dispatch(Action.Deleted(Either.Right(result.isDeleted)))
            },
            { error, _ ->
                actionDispatcher.dispatch(Action.Deleted(Either.Left(error)))
            }
        )
}