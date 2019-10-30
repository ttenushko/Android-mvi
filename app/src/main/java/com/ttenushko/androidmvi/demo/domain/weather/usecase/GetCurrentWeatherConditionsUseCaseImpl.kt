package com.ttenushko.androidmvi.demo.domain.weather.usecase

import com.ttenushko.androidmvi.demo.domain.usecase.CoroutineSingleResultUseCase
import com.ttenushko.androidmvi.demo.domain.weather.repository.WeatherForecastRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetCurrentWeatherConditionsUseCaseImpl(private val weatherForecastRepository: WeatherForecastRepository) :
    CoroutineSingleResultUseCase<GetCurrentWeatherConditionsUseCase.Param, GetCurrentWeatherConditionsUseCase.Result>(),
    GetCurrentWeatherConditionsUseCase {

    override suspend fun run(param: GetCurrentWeatherConditionsUseCase.Param): GetCurrentWeatherConditionsUseCase.Result =
        withContext(Dispatchers.IO) {
            GetCurrentWeatherConditionsUseCase.Result(weatherForecastRepository.getWeather(param.placeId))
        }
}