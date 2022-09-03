package com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.mvi

import com.ttenushko.androidmvi.demo.common.domain.weather.usecase.GetCurrentWeatherConditionsUseCase
import com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.mvi.Store.*
import com.ttenushko.androidmvi.demo.common.presentation.utils.TaskMiddleware
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.TaskExecutorFactory
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.asTask

internal fun getCurrentWeatherMiddleware(
    getCurrentWeatherConditionsUseCase: GetCurrentWeatherConditionsUseCase,
    taskExecutorFactory: TaskExecutorFactory
) =
    TaskMiddleware<Action, State, Event, GetCurrentWeatherConditionsUseCase.Param, GetCurrentWeatherConditionsUseCase.Result, Unit>(
        task = getCurrentWeatherConditionsUseCase.asTask(),
        taskExecutorFactory = taskExecutorFactory,
        startHandler = {
            dispatchAction(Action.Refreshing)
        },
        resultHandler = { result, _ ->
            dispatchAction(Action.Refreshed(Result.success(result.weather)))
        },
        errorHandler = { error, _ ->
            dispatchAction(Action.Refreshed(Result.failure(error)))
        }
    ) { action, chain, task ->
        when {
            action is Action.Intent && action.intent is Intent.Refresh -> {
                if (!task.isRunning) {
                    task.start(GetCurrentWeatherConditionsUseCase.Param(state.placeId), Unit)
                }
            }
            else -> chain.proceed()
        }
    }