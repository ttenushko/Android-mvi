package com.ttenushko.androidmvi.demo.domain.weather.repository

import com.ttenushko.androidmvi.demo.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.domain.weather.model.Weather

interface WeatherForecastRepository {
    fun findPlace(query: String): List<Place>
    fun getWeather(placeId: Long): Weather
}