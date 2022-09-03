package com.ttenushko.androidmvi.demo.common.data.weather.repository.rest.model

import com.google.gson.annotations.SerializedName

data class NetSys(
    @field:SerializedName("country") val countryCode: String
)