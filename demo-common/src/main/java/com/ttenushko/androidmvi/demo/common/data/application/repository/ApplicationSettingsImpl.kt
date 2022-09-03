package com.ttenushko.androidmvi.demo.common.data.application.repository

import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ttenushko.androidmvi.demo.common.domain.application.repository.ApplicationSettings
import com.ttenushko.androidmvi.demo.common.domain.weather.model.Place
import java.util.concurrent.CopyOnWriteArraySet

class ApplicationSettingsImpl(
    private val pref: SharedPreferences
) : ApplicationSettings {

    companion object {
        private const val KEY_PLACES = "places"
    }

    private val gson = GsonBuilder().create()
    private val placesUpdatedListeners =
        CopyOnWriteArraySet<ApplicationSettings.PlacesUpdatedListener>()

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