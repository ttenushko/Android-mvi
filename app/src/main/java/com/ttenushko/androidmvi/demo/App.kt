package com.ttenushko.androidmvi.demo

import android.app.Application
import com.squareup.picasso.Picasso
import com.ttenushko.androidmvi.demo.data.application.repository.ApplicationSettingsImpl
import com.ttenushko.androidmvi.demo.data.weather.repository.WeatherForecastRepositoryImpl
import com.ttenushko.androidmvi.demo.domain.application.repository.ApplicationSettings
import com.ttenushko.androidmvi.demo.domain.weather.repository.WeatherForecastRepository
import com.ttenushko.androidmvi.demo.platform.android.weather.datasource.OpenWeatherMapDataSource
import com.ttenushko.androidmvi.demo.platform.android.weather.datasource.rest.OpenWeatherMapApi
import com.ttenushko.androidmvi.demo.platform.android.weather.datasource.rest.OpenWeatherMapApiFactory

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    private val openWeatherMapApi: OpenWeatherMapApi by lazy {
        OpenWeatherMapApiFactory.create(
            this,
            Config.OPEN_WEATHER_MAP_API_BASE_URL,
            Config.OPEN_WEATHER_MAP_API_KEY,
            true
        )
    }
    val weatherForecastRepository: WeatherForecastRepository by lazy {
        WeatherForecastRepositoryImpl(OpenWeatherMapDataSource(openWeatherMapApi))
    }
    val applicationSettings: ApplicationSettings by lazy { ApplicationSettingsImpl(this) }
    val picasso: Picasso by lazy {
        Picasso.Builder(this)
            .loggingEnabled(true)
            //.indicatorsEnabled(true)
            .build()
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
    }
}