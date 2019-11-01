package com.ttenushko.androidmvi.demo

import android.app.Application
import com.ttenushko.androidmvi.demo.di.DaggerApplicationComponent
import com.ttenushko.androidmvi.demo.di.dependency.ComponentDependenciesProvider
import com.ttenushko.androidmvi.demo.di.dependency.HasComponentDependencies
import com.ttenushko.androidmvi.demo.di.module.ApplicationModule
import com.ttenushko.androidmvi.demo.di.module.DataModule
import javax.inject.Inject

class App : Application(), HasComponentDependencies {

    companion object {
        lateinit var instance: App
            private set
    }

    @Suppress("ProtectedInFinal")
    @Inject
    override lateinit var dependencies: ComponentDependenciesProvider
        protected set

    override fun onCreate() {
        instance = this
        super.onCreate()

        DaggerApplicationComponent.builder()
            .applicationContext(this)
            .applicationModule(ApplicationModule(Config.IS_DEBUG))
            .dataModule(DataModule(Config.IS_DEBUG))
            .build()
            .inject(this)
    }
}