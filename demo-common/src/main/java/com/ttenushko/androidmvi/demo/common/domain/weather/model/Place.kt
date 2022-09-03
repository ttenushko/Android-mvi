package com.ttenushko.androidmvi.demo.common.domain.weather.model

import com.google.gson.annotations.SerializedName

data class Place(
    @field:SerializedName("id") val id: Long,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("countyCode") val countyCode: String,
    @field:SerializedName("location") val location: Location
)