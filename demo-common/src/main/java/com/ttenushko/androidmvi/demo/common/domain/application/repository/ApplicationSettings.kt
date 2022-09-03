package com.ttenushko.androidmvi.demo.common.domain.application.repository

import com.ttenushko.androidmvi.demo.common.domain.weather.model.Place
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface ApplicationSettings {
    fun setPlaces(places: List<Place>)
    fun getPlaces(): List<Place>
    fun addPlacesUpdatedListener(listener: PlacesUpdatedListener)
    fun removePlacesUpdatedListener(listener: PlacesUpdatedListener)

    interface PlacesUpdatedListener {
        fun onPlacesUpdated(places: List<Place>)
    }
}

@Suppress("EXPERIMENTAL_API_USAGE")
fun ApplicationSettings.trackPlacesUpdated(): Flow<Unit> =
    callbackFlow {
        val listener = object : ApplicationSettings.PlacesUpdatedListener {
            override fun onPlacesUpdated(places: List<Place>) {
                trySend(Unit)
            }
        }
        addPlacesUpdatedListener(listener)
        awaitClose { removePlacesUpdatedListener(listener) }
    }