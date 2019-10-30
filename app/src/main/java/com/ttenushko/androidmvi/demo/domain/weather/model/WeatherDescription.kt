package com.ttenushko.androidmvi.demo.domain.weather.model

data class WeatherDescription(
    val id: Long,
    val main: String,
    val description: String,
    val iconUrl: String
)