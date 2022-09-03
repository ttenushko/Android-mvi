package com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.mvi

import com.ttenushko.androidmvi.demo.common.domain.application.usecase.DeletePlaceUseCase
import com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.mvi.Store.*
import com.ttenushko.androidmvi.demo.common.presentation.utils.TaskMiddleware
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.TaskExecutorFactory
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.asTask

internal fun deletePlaceMiddleware(
    deletePlaceUseCase: DeletePlaceUseCase,
    taskExecutorFactory: TaskExecutorFactory
) =
    TaskMiddleware<Action, State, Event, DeletePlaceUseCase.Param, DeletePlaceUseCase.Result, Unit>(
        task = deletePlaceUseCase.asTask(),
        taskExecutorFactory = taskExecutorFactory,
        startHandler = {
            dispatchAction(Action.Deleting)
        },
        resultHandler = { result, _ ->
            dispatchAction(Action.Deleted(Result.success(result.isDeleted)))
        },
        errorHandler = { error, _ ->
            dispatchAction(Action.Deleted(Result.failure(error)))
        }
    ) { action, chain, task ->
        when {
            action is Action.Intent && action.intent is Intent.DeleteConfirmed -> {
                if (!task.isRunning) {
                    task.start(DeletePlaceUseCase.Param(state.placeId), Unit)
                }
            }
            else -> chain.proceed()
        }
    }