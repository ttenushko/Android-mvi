package com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi

import com.ttenushko.androidmvi.MviStore
import com.ttenushko.androidmvi.demo.domain.weather.model.Weather
import com.ttenushko.androidmvi.demo.presentation.screens.home.Router

interface PlaceDetailsStore :
    MviStore<PlaceDetailsStore.Intention, PlaceDetailsStore.State, PlaceDetailsStore.Event> {

    data class State(
        val placeId: Long,
        val weather: Weather?,
        val error: Throwable?,
        val isLoading: Boolean,
        val isDeleting: Boolean
    ) {
        val isDeleteButtonVisible: Boolean
            get() = null != weather
    }

    sealed class Intention {
        object DeleteClicked : Intention()
        object Refresh : Intention()
        object DeleteConfirmed : Intention()
    }

    sealed class Event {
        object ShowDeleteConfirmation : Event()
        data class ShowError(val error: Throwable) : Event()
        data class Navigation(val destination: Router.Destination) : Event()
    }
}