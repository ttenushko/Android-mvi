package com.ttenushko.androidmvi.demo.domain.weather.usecase

import com.ttenushko.androidmvi.demo.domain.usecase.SingleResultUseCase
import com.ttenushko.androidmvi.demo.domain.weather.model.Weather

interface GetCurrentWeatherConditionsUseCase :
    SingleResultUseCase<GetCurrentWeatherConditionsUseCase.Param, GetCurrentWeatherConditionsUseCase.Result> {

    data class Param(
        val placeId: Long
    )

    data class Result(val weather: Weather)
}