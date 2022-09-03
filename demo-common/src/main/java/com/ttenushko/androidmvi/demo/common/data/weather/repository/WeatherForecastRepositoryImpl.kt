package com.ttenushko.androidmvi.demo.common.data.weather.repository

import com.ttenushko.androidmvi.demo.common.data.weather.repository.rest.OpenWeatherMapApi
import com.ttenushko.androidmvi.demo.common.data.weather.repository.rest.model.process
import com.ttenushko.androidmvi.demo.common.data.weather.repository.rest.model.toDomainModel
import com.ttenushko.androidmvi.demo.common.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.common.domain.weather.model.Weather
import com.ttenushko.androidmvi.demo.common.domain.weather.repository.WeatherForecastRepository

class WeatherForecastRepositoryImpl(
    private val openWeatherMapApi: OpenWeatherMapApi
) : WeatherForecastRepository {

    override fun findPlace(query: String): List<Place> =
        openWeatherMapApi.find(query).process().let { response ->
            response.items.map { it.toDomainModel() }
        }

    override fun getWeather(placeId: Long): Weather =
        openWeatherMapApi.getWeather(placeId).process().toDomainModel()
}