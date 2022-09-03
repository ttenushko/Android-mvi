package com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi

import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi.Store.Event
import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi.Store.State
import com.ttenushko.mvi.MviBootstrapper

internal fun bootstrapper() =
    MviBootstrapper<Action, State, Event> { state, dispatchAction, _ ->
        dispatchAction(Action.Intent(Store.Intent.SearchChanged(state.search)))
    }
