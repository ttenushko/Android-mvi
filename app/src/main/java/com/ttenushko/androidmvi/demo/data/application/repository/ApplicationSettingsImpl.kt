package com.ttenushko.androidmvi.demo.data.application.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ttenushko.androidmvi.demo.domain.application.repository.ApplicationSettings
import com.ttenushko.androidmvi.demo.domain.weather.model.Place
import java.util.concurrent.CopyOnWriteArraySet

class ApplicationSettingsImpl(context: Context) : ApplicationSettings {

    companion object {
        private const val PREF_FILE_NAME = "app_settings"
        private const val KEY_PLACES = "places"
    }

    private val pref: SharedPreferences
    private val gson = GsonBuilder().create()
    private val placesUpdatedListeners =
        CopyOnWriteArraySet<ApplicationSettings.PlacesUpdatedListener>()

    init {
        pref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    override fun setPlaces(places: List<Place>) {
        pref.edit().apply {
            putString(KEY_PLACES, gson.toJson(places))
        }.apply()
        placesUpdatedListeners.forEach { it.onPlacesUpdated(places) }
    }

    override fun getPlaces(): List<Place> =
        try {
            val placesString = pref.getString(KEY_PLACES, null)
            if (null != placesString) {
                gson.fromJson(placesString, object : TypeToken<List<Place>>() {}.type)
            } else listOf()
        } catch (error: Throwable) {
            listOf()
        }

    override fun addPlacesUpdatedListener(listener: ApplicationSettings.PlacesUpdatedListener) {
        placesUpdatedListeners.add(listener)
    }

    override fun removePlacesUpdatedListener(listener: ApplicationSettings.PlacesUpdatedListener) {
        placesUpdatedListeners.remove(listener)
    }
}