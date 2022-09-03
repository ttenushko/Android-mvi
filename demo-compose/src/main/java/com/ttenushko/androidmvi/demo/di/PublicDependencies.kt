package com.ttenushko.androidmvi.demo.di

import android.content.Context
import coil.ImageLoader
import com.ttenushko.androidmvi.demo.common.di.Dependency
import com.ttenushko.androidmvi.demo.common.domain.application.repository.ApplicationSettings
import com.ttenushko.androidmvi.demo.common.domain.weather.repository.WeatherForecastRepository
import com.ttenushko.androidmvi.demo.common.presentation.base.router.RouterProxy
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.common.presentation.utils.dagger.ApplicationContext


interface PublicDependencies : Dependency {
    fun applicationSettings(): ApplicationSettings
    fun weatherForecastRepository(): WeatherForecastRepository
    fun mainRouterProxy(): RouterProxy<MainRouter.Destination>
    fun imageLoader(): ImageLoader

    @ApplicationContext
    fun context(): Context
}