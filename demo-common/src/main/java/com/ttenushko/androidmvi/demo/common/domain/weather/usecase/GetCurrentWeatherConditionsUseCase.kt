package com.ttenushko.androidmvi.demo.common.domain.weather.usecase

import com.ttenushko.androidmvi.demo.common.domain.usecase.SingleResultUseCase
import com.ttenushko.androidmvi.demo.common.domain.weather.model.Weather

interface GetCurrentWeatherConditionsUseCase :
    SingleResultUseCase<GetCurrentWeatherConditionsUseCase.Param, GetCurrentWeatherConditionsUseCase.Result> {

    data class Param(
        val placeId: Long
    )

    data class Result(val weather: Weather)
}