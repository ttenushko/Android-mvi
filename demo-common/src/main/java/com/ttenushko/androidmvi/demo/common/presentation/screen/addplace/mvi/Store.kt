package com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi

import com.ttenushko.androidmvi.demo.common.domain.application.usecase.SavePlaceUseCase
import com.ttenushko.androidmvi.demo.common.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.common.domain.weather.usecase.SearchPlaceUseCase
import com.ttenushko.androidmvi.demo.common.presentation.base.router.Router
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.common.presentation.utils.MviLogger
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.TaskExecutorFactory
import com.ttenushko.mvi.MviStore
import com.ttenushko.mvi.createMviStore
import com.ttenushko.mvi.extra.mviLoggingMiddleware
import kotlinx.coroutines.CoroutineDispatcher

interface Store :
    MviStore<Store.Intent, Store.State, Store.Event> {

    data class State(
        val search: String,
        val searchResult: SearchResult?,
    ) {
        val isSearching: Boolean
            get() {
                return search != searchResult?.search
            }
        val isShowSearchPrompt: Boolean =
            !isSearching && search.isBlank()

        val isShowSearchErrorPrompt =
            !isSearching && searchResult is SearchResult.Failure

        val isShowSearchNoResultsPrompt =
            !isSearching && search.isNotBlank() && searchResult is SearchResult.Success && searchResult.places.isEmpty()

        sealed class SearchResult(open val search: String) {
            data class Success(
                override val search: String,
                val places: List<Place>,
            ) : SearchResult(search)

            data class Failure(
                override val search: String,
                val error: Throwable,
            ) : SearchResult(search)
        }
    }

    sealed class Intent {
        object ToolbarBackButtonClicked : Intent()
        data class SearchChanged(val search: String) : Intent()
        data class PlaceClicked(val place: Place) : Intent()
    }

    object Event

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun create(
            initialState: State,
            searchPlaceUseCase: SearchPlaceUseCase,
            savePlaceUseCase: SavePlaceUseCase,
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
                    searchPlaceMiddleware(searchPlaceUseCase, taskExecutorFactory),
                    addPlaceMiddleware(savePlaceUseCase, taskExecutorFactory)
                ),
                reducer = reducer(router),
                intentToActionConverter = { intent -> Action.Intent(intent) },
                coroutineDispatcher = coroutineDispatcher
            )
    }
}