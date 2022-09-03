package com.ttenushko.androidmvi.demo.common.presentation.screen.places.mvi

import com.ttenushko.androidmvi.demo.common.presentation.screen.places.mvi.Store.Event
import com.ttenushko.androidmvi.demo.common.presentation.screen.places.mvi.Store.State
import com.ttenushko.mvi.MviBootstrapper

internal fun bootstrapper() =
    MviBootstrapper<Action, State, Event> { _, dispatchAction, _ ->
        dispatchAction(Action.Initialize)
    }
