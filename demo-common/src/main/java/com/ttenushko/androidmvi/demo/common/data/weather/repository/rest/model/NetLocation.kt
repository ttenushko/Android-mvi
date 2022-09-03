package com.ttenushko.androidmvi.demo.common.data.weather.repository.rest.model

import com.google.gson.annotations.SerializedName

data class NetLocation(
    @field:SerializedName("lat") val latitude: Float,
    @field:SerializedName("lon") val longitude: Float
)