package com.ttenushko.androidmvi.demo.di.domain

import com.ttenushko.androidmvi.demo.common.domain.application.repository.ApplicationSettings
import com.ttenushko.androidmvi.demo.common.domain.application.usecase.*
import com.ttenushko.androidmvi.demo.common.domain.weather.repository.WeatherForecastRepository
import com.ttenushko.androidmvi.demo.common.domain.weather.usecase.GetCurrentWeatherConditionsUseCase
import com.ttenushko.androidmvi.demo.common.domain.weather.usecase.GetCurrentWeatherConditionsUseCaseImpl
import com.ttenushko.androidmvi.demo.common.domain.weather.usecase.SearchPlaceUseCase
import com.ttenushko.androidmvi.demo.common.domain.weather.usecase.SearchPlaceUseCaseImpl
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.TaskExecutorFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
class UseCaseModule {

    @Provides
    fun taskExecutorFactory(): TaskExecutorFactory =
        TaskExecutorFactory.create(CoroutineScope(Dispatchers.Main + SupervisorJob()))

    @Provides
    fun provideDeletePlaceUseCase(applicationSettings: ApplicationSettings): DeletePlaceUseCase =
        DeletePlaceUseCaseImpl(applicationSettings)

    @Provides
    fun provideGetSavedPlacesUseCase(applicationSettings: ApplicationSettings): GetSavedPlacesUseCase =
        GetSavedPlacesUseCaseImpl(applicationSettings)

    @Provides
    fun provideSavePlaceUseCase(applicationSettings: ApplicationSettings): SavePlaceUseCase =
        SavePlaceUseCaseImpl(applicationSettings)

    @Provides
    fun provideTrackSavedPlacesUseCase(applicationSettings: ApplicationSettings): TrackSavedPlacesUseCase =
        TrackSavedPlacesUseCaseImpl(applicationSettings)

    @Provides
    fun provideGetCurrentWeatherConditionsUseCase(weatherForecastRepository: WeatherForecastRepository): GetCurrentWeatherConditionsUseCase =
        GetCurrentWeatherConditionsUseCaseImpl(weatherForecastRepository)

    @Provides
    fun provideSearchPlaceUseCase(weatherForecastRepository: WeatherForecastRepository): SearchPlaceUseCase =
        SearchPlaceUseCaseImpl(weatherForecastRepository, 300)
}