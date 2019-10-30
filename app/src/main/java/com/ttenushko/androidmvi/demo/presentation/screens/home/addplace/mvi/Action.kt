package com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi

import com.ttenushko.androidmvi.demo.domain.utils.Either
import com.ttenushko.androidmvi.demo.domain.weather.model.Place

internal sealed class Action {
    data class SearchChanged(val search: String) : Action()
    data class SearchComplete(val searchResult: AddPlaceStore.State.SearchResult) : Action()
    data class PlaceClicked(val place: Place) : Action()
    data class PlaceSaved(val result: Either<Throwable, Place>) : Action()
}
