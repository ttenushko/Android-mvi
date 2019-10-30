package com.ttenushko.androidmvi.demo.domain.weather.model

data class WeatherConditions(
    val tempCurrent: Float,
    val tempMin: Float,
    val tempMax: Float,
    val pressure: Int,
    val humidity: Int
)
