package com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.mvi

import com.ttenushko.androidmvi.demo.common.presentation.base.router.Router
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.mvi.Store.State
import com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.mvi.Store.Event
import com.ttenushko.mvi.MviReducer

internal fun reducer(router: Router<MainRouter.Destination>) =
    MviReducer<Action, State, Event> { action, state, dispatchEvent ->
        when {
            action is Action.Refreshing -> {
                state.copy(isRefreshing = true)
            }
            action is Action.Refreshed -> {
                with(action.result) {
                    this.onFailure { error ->
                        dispatchEvent(Event.ShowError(error))
                    }
                    getOrNull()?.let { weather ->
                        state.copy(
                            weather = weather,
                            error = null,
                            isRefreshing = false
                        )
                    } ?: exceptionOrNull()?.let { error ->
                        state.copy(
                            weather = null,
                            error = error,
                            isRefreshing = false
                        )
                    } ?: state
                }
            }
            action is Action.Deleting -> {
                state.copy(isDeleting = true)
            }
            action is Action.Deleted -> {
                action.result
                    .onSuccess {
                        router.navigateTo(MainRouter.Destination.GoBack)
                    }
                    .onFailure { error ->
                        dispatchEvent(Event.ShowError(error))
                    }
                state.copy(isDeleting = false)
            }
            action is Action.Intent && action.intent is Store.Intent.DeleteClicked -> {
                dispatchEvent(Event.ShowDeleteConfirmation)
                state
            }
            action is Action.Intent && action.intent is Store.Intent.ToolbarBackButtonClicked -> {
                router.navigateTo(MainRouter.Destination.GoBack)
                state
            }
            else -> state
        }
    }