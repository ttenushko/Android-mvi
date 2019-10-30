package com.ttenushko.androidmvi.demo.domain.application.usecase

import com.ttenushko.androidmvi.demo.domain.usecase.SingleResultUseCase

interface DeletePlaceUseCase :
    SingleResultUseCase<DeletePlaceUseCase.Param, DeletePlaceUseCase.Result> {

    data class Param(
        val placeId: Long
    )

    data class Result(
        val isDeleted: Boolean
    )
}