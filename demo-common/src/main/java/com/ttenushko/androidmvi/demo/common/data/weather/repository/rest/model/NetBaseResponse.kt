package com.ttenushko.androidmvi.demo.common.data.weather.repository.rest.model

import com.google.gson.annotations.SerializedName

open class NetBaseResponse(
    @field:SerializedName("cod") val code: Int,
    @field:SerializedName("message") val message: String
)