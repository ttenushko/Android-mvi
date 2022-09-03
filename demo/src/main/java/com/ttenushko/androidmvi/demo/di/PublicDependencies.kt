package com.ttenushko.androidmvi.demo.di

import android.content.Context
import com.squareup.picasso.Picasso
import com.ttenushko.androidmvi.demo.common.di.Dependency
import com.ttenushko.androidmvi.demo.common.di.DependencyKey
import com.ttenushko.androidmvi.demo.common.domain.application.repository.ApplicationSettings
import com.ttenushko.androidmvi.demo.common.domain.weather.repository.WeatherForecastRepository
import com.ttenushko.androidmvi.demo.common.presentation.base.router.RouterProxy
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.common.presentation.utils.dagger.ApplicationContext
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


interface PublicDependencies : Dependency {
    fun applicationSettings(): ApplicationSettings
    fun weatherForecastRepository(): WeatherForecastRepository
    fun mainRouterProxy(): RouterProxy<MainRouter.Destination>
    fun picasso(): Picasso

    @ApplicationContext
    fun context(): Context
}

@Module
abstract class PublicDependenciesProviderModule {
    @Binds
    @IntoMap
    @DependencyKey(PublicDependencies::class)
    abstract fun publicDependencies(component: AppComponent): Dependency
}