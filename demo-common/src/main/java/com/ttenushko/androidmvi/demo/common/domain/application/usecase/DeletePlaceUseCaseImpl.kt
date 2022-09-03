package com.ttenushko.androidmvi.demo.common.domain.application.usecase

import com.ttenushko.androidmvi.demo.common.domain.application.repository.ApplicationSettings
import com.ttenushko.androidmvi.demo.common.domain.usecase.CoroutineSingleResultUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DeletePlaceUseCaseImpl(
    private val applicationSettings: ApplicationSettings,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CoroutineSingleResultUseCase<DeletePlaceUseCase.Param, DeletePlaceUseCase.Result>(ioDispatcher),
    DeletePlaceUseCase {

    override suspend fun run(param: DeletePlaceUseCase.Param): DeletePlaceUseCase.Result {
        val savedPlaces = applicationSettings.getPlaces().filter { it.id != param.placeId }
        applicationSettings.setPlaces(savedPlaces)
        return DeletePlaceUseCase.Result(true)
    }
}