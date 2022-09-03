package com.ttenushko.androidmvi.demo

import com.ttenushko.androidmvi.demo.BuildConfig.DEBUG

object Config {
    val IS_DEBUG = DEBUG

    const val OPEN_WEATHER_MAP_API_KEY = "d3032bc23866cd75cd8e41552692f896"
    const val OPEN_WEATHER_MAP_API_BASE_URL = "http://api.openweathermap.org/data/2.5/"
}

/*
 * TODO:
 * --- merge actions & events into single ValueQueueDrain
 * +++ implement side effects (like emitting navigation event) in post processor
 * +++ implement event logging
 * +++ navigation with parameters
 * +++ pass parameters to StoreCreator
 * handle back press with navigation arch component
 * +++ handle multiple toolbars with navigation arch component
 * +++ saving state
 * +++ fragment enter/exit animations
 * +++ displaying errors
 * +++ dagger2
 * +++ app style/theme
 * +++ better mvi logs: remove extra 'new state' line when state is not changed
 */