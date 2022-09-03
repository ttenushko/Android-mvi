package com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.mvi

import com.ttenushko.androidmvi.demo.common.domain.weather.model.Weather

internal sealed class Action {
    data class Intent(val intent: Store.Intent) : Action()

    object Deleting : Action() {
        override fun toString(): String =
            "Deleting"
    }

    data class Deleted(val result: Result<Boolean>) : Action()

    object Refreshing : Action() {
        override fun toString(): String =
            "Refreshing"
    }

    data class Refreshed(val result: Result<Weather>) : Action()
}