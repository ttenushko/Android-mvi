package com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi

import com.ttenushko.androidmvi.Dispatcher
import com.ttenushko.androidmvi.MviBootstrapper
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.PlaceDetailsStore.Event
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.PlaceDetailsStore.State

internal class Bootstrapper :
    MviBootstrapper<Action, State, Event> {

    override fun bootstrap(
        state: State,
        actionDispatcher: Dispatcher<Action>,
        eventDispatcher: Dispatcher<Event>
    ) {
        when {
            null != state.error -> {
                // do nothing
            }
            null == state.weather -> {
                actionDispatcher.dispatch(Action.Reload)
            }
            else -> {
                // do nothing
            }
        }
    }
}