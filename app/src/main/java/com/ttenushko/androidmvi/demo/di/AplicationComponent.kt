package com.ttenushko.androidmvi.demo.di

import android.content.Context
import com.squareup.picasso.Picasso
import com.ttenushko.androidmvi.demo.App
import com.ttenushko.androidmvi.demo.di.annotation.ApplicationScope
import com.ttenushko.androidmvi.demo.di.dependency.ComponentDependencies
import com.ttenushko.androidmvi.demo.di.dependency.ComponentDependenciesKey
import com.ttenushko.androidmvi.demo.di.module.ApplicationModule
import com.ttenushko.androidmvi.demo.di.module.DataModule
import com.ttenushko.androidmvi.demo.di.module.MviModule
import com.ttenushko.androidmvi.demo.domain.application.repository.ApplicationSettings
import com.ttenushko.androidmvi.demo.domain.weather.repository.WeatherForecastRepository
import com.ttenushko.androidmvi.demo.presentation.di.annotation.ApplicationContext
import com.ttenushko.androidmvi.demo.presentation.utils.MviEventLogger
import com.ttenushko.androidmvi.demo.presentation.utils.MviLogger
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.multibindings.IntoMap

interface ApplicationDependencies :
    ComponentDependencies {
    fun picasso(): Picasso
    fun applicationSettings(): ApplicationSettings
    fun weatherForecastRepository(): WeatherForecastRepository
    fun mviEventLogger(): MviEventLogger<Any>
    fun mviLogger(): MviLogger<Any, Any>
}

@Component(
    modules = [
        ApplicationModule::class,
        DataModule::class,
        MviModule::class,
        ComponentDependenciesModule::class
    ]
)
@ApplicationScope
interface ApplicationComponent : ApplicationDependencies {

    @Component.Builder
    interface Builder {

        @BindsInstance
        @ApplicationContext
        fun applicationContext(applicationContext: Context): Builder

        fun applicationModule(applicationModule: ApplicationModule): Builder
        fun dataModule(dataModule: DataModule): Builder
        fun build(): ApplicationComponent
    }

    fun inject(app: App)
}

@dagger.Module
abstract class ComponentDependenciesModule {
    @Binds
    @IntoMap
    @ComponentDependenciesKey(ApplicationDependencies::class)
    abstract fun applicationDependencies(component: ApplicationComponent): ComponentDependencies
}