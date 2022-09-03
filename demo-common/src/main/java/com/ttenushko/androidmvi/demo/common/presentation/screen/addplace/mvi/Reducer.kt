package com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi

import com.ttenushko.androidmvi.demo.common.presentation.base.router.Router
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi.Store.Intent
import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi.Store.State
import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi.Store.Event
import com.ttenushko.mvi.MviReducer


internal fun reducer(router: Router<MainRouter.Destination>) =
    MviReducer<Action, State, Event> { action, state, _ ->
        when {
            action is Action.Intent && action.intent is Intent.SearchChanged -> {
                if (state.search != action.intent.search) {
                    state.copy(search = action.intent.search)
                } else state
            }
            action is Action.Intent && action.intent is Intent.ToolbarBackButtonClicked -> {
                router.navigateTo(MainRouter.Destination.GoBack)
                state
            }
            action is Action.SearchComplete -> {
                state.copy(searchResult = action.searchResult)
            }
            action is Action.PlaceSaved -> {
                router.navigateTo(MainRouter.Destination.GoBack)
                state
            }
            else -> state
        }
    }