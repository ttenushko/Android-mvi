package com.ttenushko.androidmvi.demo.common.domain.application.usecase

import com.ttenushko.androidmvi.demo.common.domain.application.repository.ApplicationSettings
import com.ttenushko.androidmvi.demo.common.domain.usecase.CoroutineSingleResultUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class GetSavedPlacesUseCaseImpl(
    private val applicationSettings: ApplicationSettings,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    CoroutineSingleResultUseCase<GetSavedPlacesUseCase.Param, GetSavedPlacesUseCase.Result>(
        ioDispatcher
    ), GetSavedPlacesUseCase {

    override suspend fun run(param: GetSavedPlacesUseCase.Param): GetSavedPlacesUseCase.Result =
        GetSavedPlacesUseCase.Result(applicationSettings.getPlaces())
}