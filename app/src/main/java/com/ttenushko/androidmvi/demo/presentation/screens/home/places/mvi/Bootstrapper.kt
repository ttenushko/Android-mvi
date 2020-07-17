package com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi

import com.ttenushko.mvi.Dispatcher
import com.ttenushko.mvi.MviBootstrapper

internal class Bootstrapper :
    MviBootstrapper<Action, PlacesStore.State, PlacesStore.Event> {

    override fun bootstrap(
        state: PlacesStore.State,
        actionDispatcher: Dispatcher<Action>,
        eventDispatcher: Dispatcher<PlacesStore.Event>
    ) {
        actionDispatcher.dispatch(Action.Initialize)
    }
}