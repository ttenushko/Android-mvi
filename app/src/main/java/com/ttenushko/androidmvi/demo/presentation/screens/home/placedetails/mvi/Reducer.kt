package com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi

import com.ttenushko.androidmvi.MviReducer
import com.ttenushko.androidmvi.demo.domain.utils.Either
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.PlaceDetailsStore.State

internal class Reducer : MviReducer<Action, State> {
    override fun reduce(
        action: Action,
        state: State
    ): State {
        return when (action) {
            is Action.Reloading -> {
                state.copy(isLoading = true)
            }
            is Action.Reloaded -> {
                when (val result = action.result) {
                    is Either.Right -> {
                        state.copy(
                            weather = result.value,
                            error = null,
                            isLoading = false
                        )
                    }
                    is Either.Left -> {
                        state.copy(
                            weather = null,
                            error = result.value,
                            isLoading = false
                        )
                    }
                }
            }
            is Action.Deleting -> {
                state.copy(isDeleting = true)
            }
            is Action.Deleted -> {
                when (action.result) {
                    is Either.Right -> {
                        state.copy(
                            isDeleting = false
                        )
                    }
                    is Either.Left -> {
                        state.copy(
                            isDeleting = false
                        )
                    }
                }
            }
            else -> state
        }
    }
}