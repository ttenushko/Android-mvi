package com.ttenushko.androidmvi.demo.common.data.weather.repository.rest.model

import com.google.gson.annotations.SerializedName

data class NetWeatherConditions(
    @field:SerializedName("temp") val tempCurrent: Float,
    @field:SerializedName("temp_min") val tempMin: Float,
    @field:SerializedName("temp_max") val tempMax: Float,
    @field:SerializedName("pressure") val pressure: Int,
    @field:SerializedName("humidity") val humidity: Int
)
