package com.ttenushko.androidmvi.demo.di

import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.util.DebugLogger
import com.ttenushko.androidmvi.demo.common.di.ApplicationScope
import com.ttenushko.androidmvi.demo.common.presentation.base.router.RouterProxy
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.common.presentation.utils.dagger.ApplicationContext
import dagger.Module
import dagger.Provides

@Module
class AppModule(
    private val application: Application,
    private val isDebug: Boolean
) {
    @ApplicationContext
    @ApplicationScope
    @Provides
    fun context(): Context =
        application

    @ApplicationScope
    @Provides
    fun mainRouterProxy(): RouterProxy<MainRouter.Destination> =
        RouterProxy()

    @ApplicationScope
    @Provides
    fun imageLoader(): ImageLoader =
        ImageLoader.Builder(application)
            .apply {
                if (isDebug) {
                    logger(DebugLogger())
                }
            }
            .build()
}