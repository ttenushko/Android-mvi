package com.ttenushko.androidmvi.demo.domain.application.usecase

import com.ttenushko.androidmvi.demo.domain.application.repository.ApplicationSettings
import com.ttenushko.androidmvi.demo.domain.usecase.CoroutineSingleResultUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeletePlaceUseCaseImpl(private val applicationSettings: ApplicationSettings) :
    CoroutineSingleResultUseCase<DeletePlaceUseCase.Param, DeletePlaceUseCase.Result>(),
    DeletePlaceUseCase {

    override suspend fun run(param: DeletePlaceUseCase.Param): DeletePlaceUseCase.Result =
        withContext(Dispatchers.IO) {
            val savedPlaces = applicationSettings.getPlaces().filter { it.id != param.placeId }
            applicationSettings.setPlaces(savedPlaces)
            DeletePlaceUseCase.Result(true)
        }
}