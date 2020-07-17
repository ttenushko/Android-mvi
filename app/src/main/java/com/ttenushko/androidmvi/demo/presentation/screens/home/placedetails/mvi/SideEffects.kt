package com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi

import com.ttenushko.androidmvi.demo.domain.utils.Either
import com.ttenushko.androidmvi.demo.presentation.screens.home.Router
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.PlaceDetailsStore.Event
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.PlaceDetailsStore.State
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
            is Action.DeleteClicked -> {
                eventDispatcher.dispatch(Event.ShowDeleteConfirmation)
            }
            is Action.Deleted -> {
                when (action.result) {
                    is Either.Right -> {
                        eventDispatcher.dispatch(Event.Navigation(Router.Destination.GoBack))
                    }
                    is Either.Left -> {
                        eventDispatcher.dispatch(Event.ShowError(action.result.value))
                    }
                }
            }
        }
    }
}