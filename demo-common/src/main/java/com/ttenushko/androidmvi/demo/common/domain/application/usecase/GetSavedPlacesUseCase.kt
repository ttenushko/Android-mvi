package com.ttenushko.androidmvi.demo.common.domain.application.usecase

import com.ttenushko.androidmvi.demo.common.domain.usecase.SingleResultUseCase
import com.ttenushko.androidmvi.demo.common.domain.weather.model.Place

interface GetSavedPlacesUseCase :
    SingleResultUseCase<GetSavedPlacesUseCase.Param, GetSavedPlacesUseCase.Result> {

    class Param
    data class Result(val places: List<Place>)
}