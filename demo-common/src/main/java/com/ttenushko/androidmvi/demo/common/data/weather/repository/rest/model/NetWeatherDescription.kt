package com.ttenushko.androidmvi.demo.common.data.weather.repository.rest.model

import com.google.gson.annotations.SerializedName

data class NetWeatherDescription(
    @field:SerializedName("id") val id: Long,
    @field:SerializedName("main") val main: String,
    @field:SerializedName("description") val description: String,
    @field:SerializedName("icon") val icon: String
) {
    val iconUrl: String
        get() = "http://openweathermap.org/img/wn/${icon}@2x.png"
}