package com.ttenushko.androidmvi.demo.di

import android.content.Context
import com.ttenushko.androidmvi.demo.App
import com.ttenushko.androidmvi.demo.common.di.ApplicationScope
import com.ttenushko.androidmvi.demo.di.data.DataModule
import com.ttenushko.androidmvi.demo.common.presentation.utils.dagger.ApplicationContext
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        AppModule::class,
        DataModule::class,
        PublicDependenciesProviderModule::class
    ]
)
@ApplicationScope
interface AppComponent : PublicDependencies {

    @Component.Builder
    interface Builder {
        @BindsInstance
        @ApplicationContext
        fun applicationContext(applicationContext: Context): Builder
        fun appModule(appModule: AppModule): Builder
        fun dataModule(dataModule: DataModule): Builder
        fun build(): AppComponent
    }

    fun inject(app: App)
}