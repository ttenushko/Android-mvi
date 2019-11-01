package com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi

import com.ttenushko.androidmvi.demo.domain.utils.Either
import com.ttenushko.androidmvi.demo.domain.weather.model.Weather

internal sealed class Action {
    object DeleteClicked : Action() {
        override fun toString(): String =
            "DeleteClicked"
    }

    object Delete : Action() {
        override fun toString(): String =
            "Delete"
    }

    object Deleting : Action() {
        override fun toString(): String =
            "Deleting"
    }

    data class Deleted(val result: Either<Throwable, Boolean>) : Action()
    
    object Reload : Action() {
        override fun toString(): String =
            "Reload"
    }

    object Reloading : Action() {
        override fun toString(): String =
            "Reloading"
    }

    data class Reloaded(val result: Either<Throwable, Weather>) : Action()
}