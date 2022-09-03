package com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi

import com.ttenushko.androidmvi.demo.common.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi.Store.State.SearchResult

internal sealed class Action {
    data class Intent(val intent: Store.Intent) : Action()
    data class SearchComplete(val searchResult: SearchResult) : Action()
    data class PlaceSaved(val result: Result<Place>) : Action()
}
