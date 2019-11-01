package com.ttenushko.androidmvi.demo.di.module

import com.ttenushko.androidmvi.demo.di.annotation.ApplicationScope
import com.ttenushko.androidmvi.demo.presentation.utils.MviEventLogger
import com.ttenushko.androidmvi.demo.presentation.utils.MviLogger
import dagger.Module
import dagger.Provides

@Module
class MviModule {

    @Provides
    @ApplicationScope
    fun provideMviEventLogger(): MviEventLogger<Any> =
        MviEventLogger("_mvi_")

    @Provides
    @ApplicationScope
    fun provideMviLogger(): MviLogger<Any, Any> =
        MviLogger("_mvi_")
}