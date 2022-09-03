package com.ttenushko.androidmvi.demo.common.data.weather.repository.rest.model

class NetErrorResponse(
    code: Int,
    message: String
) : NetBaseResponse(code, message)