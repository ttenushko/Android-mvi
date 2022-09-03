package com.ttenushko.androidmvi.demo.common.domain.application.usecase

import com.ttenushko.androidmvi.demo.common.domain.usecase.SingleResultUseCase
import com.ttenushko.androidmvi.demo.common.domain.weather.model.Place

interface SavePlaceUseCase : SingleResultUseCase<SavePlaceUseCase.Param, SavePlaceUseCase.Result> {

    data class Param(
        val place: Place
    )

    data class Result(
        val place: Place,
        val isAdded: Boolean
    )
}