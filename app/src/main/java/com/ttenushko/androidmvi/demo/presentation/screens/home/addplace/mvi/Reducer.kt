package com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi


import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.AddPlaceStore.State
import com.ttenushko.mvi.MviReducer

internal class Reducer : MviReducer<Action, State> {
    override fun reduce(
        action: Action,
        state: State
    ): State {
        var updatedState = when (action) {
            is Action.SearchChanged -> {
                if (state.search != action.search) {
                    state.copy(
                        search = action.search,
                        isSearching = true
                    )
                } else state
            }
            is Action.SearchComplete -> {
                if (state.search == action.searchResult.search) {
                    state.copy(
                        searchResult = action.searchResult,
                        isSearching = false
                    )
                } else state
            }
            else -> state
        }
        val isSearching = updatedState.search != updatedState.searchResult.search
        if (isSearching != updatedState.isSearching) {
            updatedState = updatedState.copy(isSearching = isSearching)
        }
        return updatedState
    }
}