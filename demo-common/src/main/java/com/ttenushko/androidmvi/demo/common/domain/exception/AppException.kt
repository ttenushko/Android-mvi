package com.ttenushko.androidmvi.demo.common.domain.exception

open class AppException(msg: String? = null, cause: Throwable? = null) : Exception(msg, cause)