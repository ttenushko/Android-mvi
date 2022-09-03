package com.ttenushko.androidmvi.demo.common.domain.weather.repository

import com.ttenushko.androidmvi.demo.common.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.common.domain.weather.model.Weather

interface WeatherForecastRepository {
    fun findPlace(query: String): List<Place>
    fun getWeather(placeId: Long): Weather
}