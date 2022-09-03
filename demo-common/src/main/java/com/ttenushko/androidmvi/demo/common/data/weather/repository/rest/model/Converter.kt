package com.ttenushko.androidmvi.demo.common.data.weather.repository.rest.model

import com.ttenushko.androidmvi.demo.common.domain.weather.model.*


fun NetLocation.toDomainModel(): Location =
    Location(latitude, longitude)

fun NetPlace.toDomainModel(): Place =
    Place(id, name, sys.countryCode, location.toDomainModel())

fun NetWeatherConditions.toDomainModel(): WeatherConditions =
    WeatherConditions(tempCurrent, tempMin, tempMax, pressure, humidity)

fun NetWeatherDescription.toDomainModel(): WeatherDescription =
    WeatherDescription(id, main, description, iconUrl)

fun NetWeather.toDomainModel(): Weather =
    Weather(
        Place(id, name, sys.countryCode, location.toDomainModel()),
        conditions.toDomainModel(),
        descriptions.map { it.toDomainModel() }
    )