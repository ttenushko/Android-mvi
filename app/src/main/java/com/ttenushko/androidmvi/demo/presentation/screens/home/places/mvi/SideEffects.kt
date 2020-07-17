package com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi

import com.ttenushko.androidmvi.demo.presentation.screens.home.Router
import com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi.PlacesStore.Event
import com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi.PlacesStore.State
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
            is Action.AddPlaceButtonClicked -> {
                eventDispatcher.dispatch(Event.Navigation(Router.Destination.AddPlace("")))
            }
            is Action.PlaceClicked -> {
                eventDispatcher.dispatch(Event.Navigation(Router.Destination.PlaceDetails(action.place.id)))
            }
        }
    }
}