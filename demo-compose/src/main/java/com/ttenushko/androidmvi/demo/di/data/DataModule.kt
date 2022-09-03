package com.ttenushko.androidmvi.demo.di.data

import android.content.Context
import com.ttenushko.androidmvi.demo.Config
import com.ttenushko.androidmvi.demo.common.data.application.repository.ApplicationSettingsImpl
import com.ttenushko.androidmvi.demo.common.data.weather.repository.WeatherForecastRepositoryImpl
import com.ttenushko.androidmvi.demo.common.data.weather.repository.rest.OpenWeatherMapApi
import com.ttenushko.androidmvi.demo.common.data.weather.repository.rest.OpenWeatherMapApiFactory
import com.ttenushko.androidmvi.demo.common.di.ApplicationScope
import com.ttenushko.androidmvi.demo.common.domain.application.repository.ApplicationSettings
import com.ttenushko.androidmvi.demo.common.domain.weather.repository.WeatherForecastRepository
import dagger.Module
import dagger.Provides


@Module
class DataModule(
    private val isDebug: Boolean
) {
    @Provides
    @ApplicationScope
    fun provideApplicationSettings(context: Context): ApplicationSettings =
        ApplicationSettingsImpl(
            context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        )

    @Provides
    @ApplicationScope
    fun provideOpenWeatherMapApi(context: Context): OpenWeatherMapApi =
        OpenWeatherMapApiFactory.create(
            context,
            Config.OPEN_WEATHER_MAP_API_BASE_URL,
            Config.OPEN_WEATHER_MAP_API_KEY,
            isDebug
        )

    @Provides
    @ApplicationScope
    fun provideWeatherForecastRepository(openWeatherMapApi: OpenWeatherMapApi): WeatherForecastRepository =
        WeatherForecastRepositoryImpl(openWeatherMapApi)
}