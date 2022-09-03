package com.ttenushko.androidmvi.demo.common.domain.weather.model

data class Weather(
    val place: Place,
    val conditions: WeatherConditions,
    val descriptions: List<WeatherDescription>
)