package com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi

import com.ttenushko.androidmvi.MviMiddleware
import com.ttenushko.androidmvi.MviMiddlewareImpl
import com.ttenushko.androidmvi.demo.domain.utils.ObservableValue
import com.ttenushko.androidmvi.demo.domain.weather.usecase.SearchPlaceUseCase
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.AddPlaceStore.Event
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.AddPlaceStore.State
import com.ttenushko.androidmvi.demo.presentation.utils.task.MultiResultTask
import com.ttenushko.androidmvi.demo.presentation.utils.task.asMultiResultJob
import kotlinx.coroutines.Dispatchers

internal class SearchPlaceMiddleware(
    private val searchPlaceUseCase: SearchPlaceUseCase
) : MviMiddlewareImpl<Action, State, Event>() {

    private val search = ObservableValue("")
    private val taskSearchPlace = createSearchPlaceTask()

    override fun apply(chain: MviMiddleware.Chain<Action, State, Event>) {
        super.apply(chain)
        when (val action = chain.action) {
            is Action.SearchChanged -> {
                chain.proceed()

                val searchText = action.search
                search.set(searchText)
                if (!taskSearchPlace.isRunning) {
                    taskSearchPlace.start(SearchPlaceUseCase.Param(search), Unit)
                }
            }
            else -> chain.proceed()
        }
    }

    override fun onClose() {
        taskSearchPlace.stop()
    }

    private fun createSearchPlaceTask() =
        MultiResultTask<SearchPlaceUseCase.Param, SearchPlaceUseCase.Result, Unit>(
            "searchPlace",
            Dispatchers.Main,
            { param, tag ->
                searchPlaceUseCase.asMultiResultJob(param, tag)
            },
            { result, _ ->
                when (result) {
                    is SearchPlaceUseCase.Result.Success -> {
                        actionDispatcher.dispatch(
                            Action.SearchComplete(
                                State.SearchResult.Success(
                                    result.search,
                                    result.places
                                )
                            )
                        )
                    }
                    is SearchPlaceUseCase.Result.Failure -> {
                        actionDispatcher.dispatch(
                            Action.SearchComplete(
                                State.SearchResult.Failure(
                                    result.search,
                                    result.error
                                )
                            )
                        )
                    }
                }

            },
            { error, _ ->
                actionDispatcher.dispatch(
                    Action.SearchComplete(
                        State.SearchResult.Failure(
                            "",
                            error
                        )
                    )
                )
            }
        )
}