package com.ttenushko.androidmvi.demo.common.domain.exception.api

import com.ttenushko.androidmvi.demo.common.domain.exception.AppException


class OpenWeatherMapApiException(
    val code: Int,
    val apiMessage: String? = null,
    exception: Exception? = null
) : AppException(apiMessage, exception)