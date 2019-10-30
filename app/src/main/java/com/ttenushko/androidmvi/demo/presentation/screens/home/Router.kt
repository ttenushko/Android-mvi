package com.ttenushko.androidmvi.demo.presentation.screens.home

interface Router {
    fun navigateTo(destination: Destination)

    sealed class Destination {
        object GoBack : Destination()
        data class AddPlace(val search: String) : Destination()
        data class PlaceDetails(val placeId: Long) : Destination()
    }
}