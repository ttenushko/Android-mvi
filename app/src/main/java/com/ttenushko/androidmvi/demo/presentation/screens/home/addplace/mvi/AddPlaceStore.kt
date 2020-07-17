package com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinToString
import com.ttenushko.androidmvi.demo.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.presentation.screens.home.Router
import com.ttenushko.mvi.MviStore
import java.util.*

interface AddPlaceStore :
    MviStore<AddPlaceStore.Intention, AddPlaceStore.State, AddPlaceStore.Event> {

    data class State(
        val search: String,
        val searchResult: SearchResult,
        val isSearching: Boolean
    ) {
        val isShowSearchPrompt: Boolean =
            !isSearching && search.isBlank()

        val isShowSearchErrorPrompt =
            !isSearching && searchResult is SearchResult.Failure

        val isShowSearchNoResultsPrompt =
            !isSearching && search.isNotBlank() && searchResult is SearchResult.Success && searchResult.places.isEmpty()

        sealed class SearchResult(val search: String) {

            class Success(
                search: String,
                val places: List<Place>
            ) : SearchResult(search) {

                companion object {
                    private val properties = arrayOf(Success::search, Success::places)
                }

                override fun equals(other: Any?) =
                    kotlinEquals(other = other, properties = properties)

                override fun toString() =
                    kotlinToString(properties = properties)

                override fun hashCode() =
                    Objects.hash(search, places)
            }

            class Failure(
                search: String,
                val error: Throwable
            ) : SearchResult(search) {

                companion object {
                    private val properties = arrayOf(Failure::search, Failure::error)
                }

                override fun equals(other: Any?) =
                    kotlinEquals(other = other, properties = properties)

                override fun toString() =
                    kotlinToString(properties = properties)

                override fun hashCode() =
                    Objects.hash(search, error)
            }
        }
    }

    sealed class Intention {
        data class SearchChanged(val search: String) : Intention()
        data class PlaceClicked(val place: Place) : Intention()
    }

    sealed class Event {
        data class Navigation(val destination: Router.Destination) : Event()
    }
}