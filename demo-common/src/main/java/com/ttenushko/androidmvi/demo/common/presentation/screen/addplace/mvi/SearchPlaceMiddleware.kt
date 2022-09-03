package com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi

import com.ttenushko.androidmvi.demo.common.domain.utils.MutableObservableValue
import com.ttenushko.androidmvi.demo.common.domain.weather.usecase.SearchPlaceUseCase
import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi.Store.Event
import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi.Store.State
import com.ttenushko.androidmvi.demo.common.presentation.utils.TaskMiddleware
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.TaskExecutorFactory
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.asTask

internal fun searchPlaceMiddleware(
    searchPlaceUseCase: SearchPlaceUseCase,
    taskExecutorFactory: TaskExecutorFactory
) =
    MutableObservableValue("", false).let { search ->
        TaskMiddleware<Action, State, Event, SearchPlaceUseCase.Param, SearchPlaceUseCase.Result, Unit>(
            task = searchPlaceUseCase.asTask(),
            taskExecutorFactory = taskExecutorFactory,
            resultHandler = { result, _ ->
                val action = when (result) {
                    is SearchPlaceUseCase.Result.Success -> {
                        Action.SearchComplete(
                            State.SearchResult.Success(
                                result.search,
                                result.places
                            )
                        )
                    }
                    is SearchPlaceUseCase.Result.Failure -> {
                        Action.SearchComplete(
                            State.SearchResult.Failure(
                                result.search,
                                result.error
                            )
                        )
                    }
                }
                dispatchAction(action)
            }
        ) { _, chain, task ->
            chain.proceed()
            search.value = state.search
            if (!task.isRunning) {
                task.start(SearchPlaceUseCase.Param(search), Unit)
            }
        }
    }