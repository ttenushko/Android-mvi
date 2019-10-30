package com.ttenushko.androidmvi.demo.domain.application.usecase

import com.ttenushko.androidmvi.demo.domain.application.repository.ApplicationSettings
import com.ttenushko.androidmvi.demo.domain.usecase.CoroutineSingleResultUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetSavedPlacesUseCaseImpl(private val applicationSettings: ApplicationSettings) :
    CoroutineSingleResultUseCase<GetSavedPlacesUseCase.Param, GetSavedPlacesUseCase.Result>(),
    GetSavedPlacesUseCase {

    override suspend fun run(param: GetSavedPlacesUseCase.Param): GetSavedPlacesUseCase.Result =
        withContext(Dispatchers.IO) {
            GetSavedPlacesUseCase.Result(applicationSettings.getPlaces())
        }
}