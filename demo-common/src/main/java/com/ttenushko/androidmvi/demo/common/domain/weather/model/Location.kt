package com.ttenushko.androidmvi.demo.common.domain.weather.model

import com.google.gson.annotations.SerializedName

data class Location(
    @field:SerializedName("latitude") val latitude: Float,
    @field:SerializedName("longitude") val longitude: Float
)