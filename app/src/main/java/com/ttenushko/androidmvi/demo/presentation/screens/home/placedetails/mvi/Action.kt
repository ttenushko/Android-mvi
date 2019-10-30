package com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi

import com.ttenushko.androidmvi.demo.domain.utils.Either
import com.ttenushko.androidmvi.demo.domain.weather.model.Weather

internal sealed class Action {
    object DeleteClicked : Action()
    object Delete : Action()
    object Deleting : Action()
    data class Deleted(val result: Either<Throwable, Boolean>) : Action()
    object Reload : Action()
    object Reloading : Action()
    data class Reloaded(val result: Either<Throwable, Weather>) : Action()
}