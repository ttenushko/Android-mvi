package com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi

import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.AddPlaceStore.Event
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.AddPlaceStore.State
import com.ttenushko.mvi.Dispatcher
import com.ttenushko.mvi.MviBootstrapper

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