package com.ttenushko.androidmvi.demo.common.data.weather.repository.rest.model

import com.google.gson.annotations.SerializedName

data class NetPlace(
    @field:SerializedName("id") val id: Long,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("coord") val location: NetLocation,
    @field:SerializedName("sys") val sys: NetSys
)