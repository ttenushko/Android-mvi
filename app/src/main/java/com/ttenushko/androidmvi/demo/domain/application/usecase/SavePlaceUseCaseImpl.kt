package com.ttenushko.androidmvi.demo.domain.application.usecase

import com.ttenushko.androidmvi.demo.domain.application.repository.ApplicationSettings
import com.ttenushko.androidmvi.demo.domain.usecase.CoroutineSingleResultUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SavePlaceUseCaseImpl(private val applicationSettings: ApplicationSettings) :
    CoroutineSingleResultUseCase<SavePlaceUseCase.Param, SavePlaceUseCase.Result>(),
    SavePlaceUseCase {

    override suspend fun run(param: SavePlaceUseCase.Param): SavePlaceUseCase.Result =
        withContext(Dispatchers.IO) {
            val savedPlaces = applicationSettings.getPlaces()
            if (!savedPlaces.contains(param.place)) {
                applicationSettings.setPlaces(savedPlaces.plus(param.place))
                SavePlaceUseCase.Result(param.place, true)
            } else {
                SavePlaceUseCase.Result(param.place, false)
            }
        }
}