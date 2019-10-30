package com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi

import com.ttenushko.androidmvi.MviStore
import com.ttenushko.androidmvi.demo.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.presentation.screens.home.Router

interface PlacesStore :
    MviStore<PlacesStore.Intention, PlacesStore.State, PlacesStore.Event> {

    data class State(
        val places: List<Place>?,
        val error: Throwable?,
        val isLoading: Boolean
    )

    sealed class Intention {
        object AddPlaceButtonClicked : Intention()
        data class PlaceClicked(val place: Place) : Intention()
    }

    sealed class Event {
        data class Navigation(val destination: Router.Destination) : Event()
    }
}