package com.ttenushko.androidmvi.demo.common.domain.application.usecase

import com.ttenushko.androidmvi.demo.common.domain.usecase.MultiResultUseCase
import com.ttenushko.androidmvi.demo.common.domain.weather.model.Place

interface TrackSavedPlacesUseCase :
    MultiResultUseCase<TrackSavedPlacesUseCase.Param, TrackSavedPlacesUseCase.Result> {

    object Param

    data class Result(val places: List<Place>)
}