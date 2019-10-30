package com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi

import com.ttenushko.androidmvi.demo.domain.utils.Either
import com.ttenushko.androidmvi.demo.domain.weather.model.Place

internal sealed class Action {

    object AddPlaceButtonClicked : Action() {
        override fun toString(): String =
            "AddPlaceButtonClicked"
    }

    data class PlaceClicked(val place: Place) : Action()

    object Initialize : Action() {
        override fun toString(): String =
            "Initialize"
    }

    object LoadingSavedPlaces : Action() {
        override fun toString(): String =
            "LoadingSavedPlaces"
    }

    data class SavedPlacesUpdated(val result: Either<Throwable, List<Place>>) : Action()
}