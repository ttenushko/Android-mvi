package com.ttenushko.androidmvi.demo.common.domain.exception.network

import com.ttenushko.androidmvi.demo.common.domain.exception.AppException


class HttpException(
    val httpCode: Int,
    val httpMessage: String? = null,
    exception: Exception? = null
) : AppException(httpMessage, exception)