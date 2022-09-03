package com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.mvi

import com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.mvi.Store.Event
import com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.mvi.Store.State
import com.ttenushko.mvi.MviBootstrapper
import com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.mvi.Store.*

internal fun bootstrapper() =
    MviBootstrapper<Action, State, Event> { state, dispatchAction, _ ->
        when {
            null != state.error -> {
                // do nothing
            }
            null == state.weather -> {
                dispatchAction(Action.Intent(Intent.Refresh))
            }
            else -> {
                // do nothing
            }
        }
    }

