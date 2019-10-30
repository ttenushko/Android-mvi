package com.ttenushko.androidmvi.demo.platform.android.weather.datasource.rest.model

import com.google.gson.annotations.SerializedName

data class NetSys(
    @field:SerializedName("country") val countryCode: String
)