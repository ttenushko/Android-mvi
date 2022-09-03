package com.ttenushko.androidmvi.demo.common.data.weather.repository.rest.model

import com.google.gson.annotations.SerializedName

class NetFindResponse(
    code: Int,
    message: String,
    @field:SerializedName("list") val items: List<NetPlace>
) : NetBaseResponse(code, message)