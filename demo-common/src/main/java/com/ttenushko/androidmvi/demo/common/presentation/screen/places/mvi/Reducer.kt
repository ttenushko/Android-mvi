package com.ttenushko.androidmvi.demo.common.presentation.screen.places.mvi

import com.ttenushko.androidmvi.demo.common.presentation.base.router.Router
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.common.presentation.screen.places.mvi.Store.Event
import com.ttenushko.androidmvi.demo.common.presentation.screen.places.mvi.Store.State
import com.ttenushko.mvi.MviReducer

internal fun reducer(router: Router<MainRouter.Destination>) =
    MviReducer<Action, State, Event> { action, state, dispatchEvent ->
        when (action) {
            is Action.SavedPlacesUpdated -> {
                with(action.result) {
                    getOrNull()?.let { state.copy(places = it, error = null) }
                        ?: exceptionOrNull()?.let { state.copy(places = null, error = it) }
                        ?: state
                }
            }
            is Action.Intent -> {
                when (action.intent) {
                    is Store.Intent.AddPlaceButtonClicked -> {
                        router.navigateTo(MainRouter.Destination.AddPlace(""))
                    }
                    is Store.Intent.PlaceClicked -> {
                        router.navigateTo(MainRouter.Destination.PlaceDetails(action.intent.place.id))
                    }
                }
                state
            }
            else -> state
        }
    }