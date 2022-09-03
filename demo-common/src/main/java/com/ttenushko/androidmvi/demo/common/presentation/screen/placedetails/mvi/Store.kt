package com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.mvi

import com.ttenushko.androidmvi.demo.common.domain.application.usecase.DeletePlaceUseCase
import com.ttenushko.androidmvi.demo.common.domain.weather.model.Weather
import com.ttenushko.androidmvi.demo.common.domain.weather.usecase.GetCurrentWeatherConditionsUseCase
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
        val placeId: Long,
        val weather: Weather?,
        val error: Throwable?,
        val isRefreshing: Boolean,
        val isDeleting: Boolean
    ) {
        val isDeleteButtonVisible: Boolean
            get() = null != weather
    }

    sealed class Intent {
        object ToolbarBackButtonClicked : Intent() {
            override fun toString(): String =
                "ToolbarBackButtonClicked"
        }

        object Refresh : Intent() {
            override fun toString(): String =
                "Refresh"
        }

        object DeleteClicked : Intent() {
            override fun toString(): String =
                "DeleteClicked"
        }

        object DeleteConfirmed : Intent() {
            override fun toString(): String =
                "DeleteConfirmed"
        }
    }

    sealed class Event {
        object ShowDeleteConfirmation : Event() {
            override fun toString(): String =
                "ShowDeleteConfirmation"
        }

        data class ShowError(val error: Throwable) : Event()
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun create(
            initialState: State,
            getCurrentWeatherConditionsUseCase: GetCurrentWeatherConditionsUseCase,
            deletePlaceUseCase: DeletePlaceUseCase,
            taskExecutorFactory: TaskExecutorFactory,
            mviLogger: MviLogger<*, *>,
            router: Router<MainRouter.Destination>,
            coroutineDispatcher: CoroutineDispatcher
        ): MviStore<Intent, State, Event> =
            createMviStore(
                initialState = initialState,
                bootstrapper = bootstrapper(),
                middleware = listOf(
                    mviLoggingMiddleware(mviLogger as MviLogger<Action, State>),
                    getCurrentWeatherMiddleware(
                        getCurrentWeatherConditionsUseCase,
                        taskExecutorFactory
                    ),
                    deletePlaceMiddleware(
                        deletePlaceUseCase,
                        taskExecutorFactory
                    )
                ),
                reducer = reducer(router),
                intentToActionConverter = { intent -> Action.Intent(intent) },
                coroutineDispatcher = coroutineDispatcher
            )
    }
}