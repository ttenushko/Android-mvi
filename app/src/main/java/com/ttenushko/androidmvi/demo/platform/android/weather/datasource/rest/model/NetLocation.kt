package com.ttenushko.androidmvi.demo.platform.android.weather.datasource.rest.model

import com.google.gson.annotations.SerializedName

data class NetLocation(
    @field:SerializedName("lat") val latitude: Float,
    @field:SerializedName("lon") val longitude: Float
)