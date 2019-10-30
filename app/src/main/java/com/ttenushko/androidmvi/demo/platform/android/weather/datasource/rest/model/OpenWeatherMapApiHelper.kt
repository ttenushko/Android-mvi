package com.ttenushko.androidmvi.demo.platform.android.weather.datasource.rest.model

import com.google.gson.GsonBuilder
import com.ttenushko.androidmvi.demo.domain.exception.api.OpenWeatherMapApiException
import com.ttenushko.androidmvi.demo.domain.exception.network.HttpException
import retrofit2.Call
import retrofit2.Response

fun <T> Call<T>.process(): T = execute().let {
    if (it.isSuccessful) it.body()!!
    else throw it.handleError()
}

private fun <T> Response<T>.handleError(): Throwable {
    require(!this.isSuccessful)
    return if (this.code() > 400) {
        try {
            val errorBodyString = this.errorBody()?.string() ?: ""
            val apiErrorResponse =
                GsonBuilder().create().fromJson(errorBodyString, NetErrorResponse::class.java)
            OpenWeatherMapApiException(apiErrorResponse.code, apiErrorResponse.message)
        } catch (error: Throwable) {
            HttpException(this.code(), this.message())
        }
    } else HttpException(this.code(), this.message())
}
