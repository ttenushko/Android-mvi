package com.ttenushko.androidmvi.demo.data.weather.repository

import com.ttenushko.androidmvi.demo.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.domain.weather.model.Weather
import com.ttenushko.androidmvi.demo.domain.weather.repository.WeatherForecastRepository

class WeatherForecastRepositoryImpl(
    private val dataSource: DataSource
) : WeatherForecastRepository {

    override fun findPlace(query: String): List<Place> =
        dataSource.findPlaces(query)

    override fun getWeather(placeId: Long): Weather =
        dataSource.getWeatherById(placeId)

    interface DataSource {
        fun findPlaces(query: String): List<Place>
        fun getWeatherById(placeId: Long): Weather
    }
}