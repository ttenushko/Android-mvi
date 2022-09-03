package com.ttenushko.androidmvi.demo.common.data.weather.repository.rest

import android.content.Context
import android.net.TrafficStats
import com.google.gson.GsonBuilder
import com.ttenushko.androidmvi.demo.common.data.utils.DelegatingSocketFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Socket
import java.util.concurrent.TimeUnit

object OpenWeatherMapApiFactory {
    private const val HTTP_CONNECT_TIMEOUT_MS = 20 * 1000
    private const val HTTP_READ_TIMEOUT_MS = 20 * 1000

    @Suppress("UNUSED_PARAMETER")
    fun create(
        context: Context,
        baseUrl: String,
        apiKey: String,
        enableLogging: Boolean
    ): OpenWeatherMapApi {
        val okHttpClient = OkHttpClient.Builder().apply {
            connectTimeout(HTTP_CONNECT_TIMEOUT_MS.toLong(), TimeUnit.MILLISECONDS)
            readTimeout(HTTP_READ_TIMEOUT_MS.toLong(), TimeUnit.MILLISECONDS)
            addInterceptor { chain ->
                val origRequest = chain.request()
                val origUrl = origRequest.url
                val updatedUrl = origUrl.newBuilder()
                    .addQueryParameter("appid", apiKey)
                    .build()
                val updatedRequest = origRequest.newBuilder()
                    .url(updatedUrl)
                    .build()
                chain.proceed(updatedRequest)
            }
            socketFactory(object : DelegatingSocketFactory(getDefault()) {
                override fun configureSocket(socket: Socket): Socket {
                    TrafficStats.tagSocket(socket)
                    return socket
                }
            })
            if (enableLogging) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
        }.build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .create()
                )
            )
            .client(okHttpClient)
            .build()
        return retrofit.create(OpenWeatherMapApi::class.java)
    }
}