package com.ttenushko.androidmvi.demo.domain.weather.model

data class Weather(
    val place: Place,
    val conditions: WeatherConditions,
    val descriptions: List<WeatherDescription>
)