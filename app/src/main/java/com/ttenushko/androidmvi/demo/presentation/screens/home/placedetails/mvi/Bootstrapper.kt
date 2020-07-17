package com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi

import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.PlaceDetailsStore.Event
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.PlaceDetailsStore.State
import com.ttenushko.mvi.Dispatcher
import com.ttenushko.mvi.MviBootstrapper

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