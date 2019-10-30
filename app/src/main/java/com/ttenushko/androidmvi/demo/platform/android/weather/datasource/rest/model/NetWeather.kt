package com.ttenushko.androidmvi.demo.platform.android.weather.datasource.rest.model

import com.google.gson.annotations.SerializedName

data class NetWeather(
    @field:SerializedName("id") val id: Long,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("coord") val location: NetLocation,
    @field:SerializedName("sys") val sys: NetSys,
    @field:SerializedName("main") val conditions: NetWeatherConditions,
    @field:SerializedName("weather") val descriptions: List<NetWeatherDescription>
)