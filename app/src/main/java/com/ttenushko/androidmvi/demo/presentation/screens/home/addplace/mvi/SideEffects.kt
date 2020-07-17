package com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi

import com.ttenushko.androidmvi.demo.presentation.screens.home.Router
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.AddPlaceStore.Event
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.AddPlaceStore.State
import com.ttenushko.mvi.Dispatcher
import com.ttenushko.mvi.MviPostProcessorMiddleware

internal class SideEffects : MviPostProcessorMiddleware.PostProcessor<Action, State, Event> {
    override fun process(
        action: Action,
        oldState: State,
        newState: State,
        actionDispatcher: Dispatcher<Action>,
        eventDispatcher: Dispatcher<Event>
    ) {
        when (action) {
            is Action.PlaceSaved -> {
                eventDispatcher.dispatch(Event.Navigation(Router.Destination.GoBack))
            }
        }
    }
}