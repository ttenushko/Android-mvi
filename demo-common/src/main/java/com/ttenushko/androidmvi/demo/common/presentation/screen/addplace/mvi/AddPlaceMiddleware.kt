package com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi

import com.ttenushko.androidmvi.demo.common.domain.application.usecase.SavePlaceUseCase
import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi.Store.*
import com.ttenushko.androidmvi.demo.common.presentation.utils.TaskMiddleware
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.TaskExecutorFactory
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.asTask

internal fun addPlaceMiddleware(
    savePlaceUseCase: SavePlaceUseCase,
    taskExecutorFactory: TaskExecutorFactory,
) =
    TaskMiddleware<Action, State, Event, SavePlaceUseCase.Param, SavePlaceUseCase.Result, Unit>(
        task = savePlaceUseCase.asTask(),
        taskExecutorFactory = taskExecutorFactory,
        resultHandler = { result, _ ->
            dispatchAction(Action.PlaceSaved(Result.success(result.place)))
        },
        errorHandler = { error, _ ->
            dispatchAction(Action.PlaceSaved(Result.failure(error)))
        }
    ) { action, chain, task ->
        when {
            action is Action.Intent && action.intent is Intent.PlaceClicked -> {
                if (!task.isRunning) {
                    task.start(SavePlaceUseCase.Param(action.intent.place), Unit)
                }
            }
            else -> chain.proceed()
        }
    }