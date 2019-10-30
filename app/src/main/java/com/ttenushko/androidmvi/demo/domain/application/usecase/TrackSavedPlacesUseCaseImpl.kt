package com.ttenushko.androidmvi.demo.domain.application.usecase

import com.ttenushko.androidmvi.demo.domain.application.repository.ApplicationSettings
import com.ttenushko.androidmvi.demo.domain.usecase.CoroutineMultiResultUseCase
import com.ttenushko.androidmvi.demo.domain.usecase.awaitCancellation
import com.ttenushko.androidmvi.demo.domain.weather.model.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.withContext

class TrackSavedPlacesUseCaseImpl(private val applicationSettings: ApplicationSettings) :
    CoroutineMultiResultUseCase<TrackSavedPlacesUseCase.Param, TrackSavedPlacesUseCase.Result>(),
    TrackSavedPlacesUseCase {

    override suspend fun run(
        param: TrackSavedPlacesUseCase.Param,
        channel: SendChannel<TrackSavedPlacesUseCase.Result>
    ) {
        withContext(Dispatchers.IO) {
            val listener = object : ApplicationSettings.PlacesUpdatedListener {
                override fun onPlacesUpdated(places: List<Place>) {
                    channel.offer(TrackSavedPlacesUseCase.Result(places))
                }
            }
            applicationSettings.addPlacesUpdatedListener(listener)
            channel.offer(TrackSavedPlacesUseCase.Result(applicationSettings.getPlaces()))
            awaitCancellation { applicationSettings.removePlacesUpdatedListener(listener) }
        }
    }
}