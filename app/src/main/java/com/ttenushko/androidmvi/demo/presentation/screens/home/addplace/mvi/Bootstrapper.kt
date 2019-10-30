package com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi

import com.ttenushko.androidmvi.Dispatcher
import com.ttenushko.androidmvi.MviBootstrapper
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.AddPlaceStore.Event
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.AddPlaceStore.State

internal class Bootstrapper :
    MviBootstrapper<Action, State, Event> {

    override fun bootstrap(
        state: State,
        actionDispatcher: Dispatcher<Action>,
        eventDispatcher: Dispatcher<Event>
    ) {
        actionDispatcher.dispatch(Action.SearchChanged(state.search))
    }
}