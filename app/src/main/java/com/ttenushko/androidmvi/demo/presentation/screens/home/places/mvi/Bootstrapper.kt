package com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi

import com.ttenushko.androidmvi.Dispatcher
import com.ttenushko.androidmvi.MviBootstrapper

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