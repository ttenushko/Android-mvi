package com.ttenushko.androidmvi.demo.platform.android.weather.datasource.rest.model

class NetErrorResponse(
    code: Int,
    message: String
) : NetBaseResponse(code, message)