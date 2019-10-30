package com.ttenushko.androidmvi.demo.domain.application.usecase

import com.ttenushko.androidmvi.demo.domain.usecase.MultiResultUseCase
import com.ttenushko.androidmvi.demo.domain.weather.model.Place

interface TrackSavedPlacesUseCase :
    MultiResultUseCase<TrackSavedPlacesUseCase.Param, TrackSavedPlacesUseCase.Result> {

    class Param
    data class Result(val places: List<Place>)
}