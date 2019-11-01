package com.ttenushko.androidmvi.demo.di.module

import android.content.Context
import com.squareup.picasso.Picasso
import com.ttenushko.androidmvi.demo.di.annotation.ApplicationScope
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule(
    private val isDebug: Boolean
) {
    @Provides
    @ApplicationScope
    fun providePicasso(context: Context): Picasso {
        val builder = Picasso.Builder(context.applicationContext)
        if (isDebug) {
            builder.loggingEnabled(true)
            //builder.indicatorsEnabled(true)
        }
        return builder.build()
    }
}