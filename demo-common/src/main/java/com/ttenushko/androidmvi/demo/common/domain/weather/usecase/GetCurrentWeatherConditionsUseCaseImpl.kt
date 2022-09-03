package com.ttenushko.androidmvi.demo.common.domain.weather.usecase

import com.ttenushko.androidmvi.demo.common.domain.usecase.CoroutineSingleResultUseCase
import com.ttenushko.androidmvi.demo.common.domain.weather.repository.WeatherForecastRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class GetCurrentWeatherConditionsUseCaseImpl(
    private val weatherForecastRepository: WeatherForecastRepository,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CoroutineSingleResultUseCase<GetCurrentWeatherConditionsUseCase.Param, GetCurrentWeatherConditionsUseCase.Result>(
    ioDispatcher
), GetCurrentWeatherConditionsUseCase {

    override suspend fun run(param: GetCurrentWeatherConditionsUseCase.Param): GetCurrentWeatherConditionsUseCase.Result =
        GetCurrentWeatherConditionsUseCase.Result(weatherForecastRepository.getWeather(param.placeId))
}