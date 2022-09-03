package com.ttenushko.androidmvi.demo.common.domain.weather.usecase

import com.ttenushko.androidmvi.demo.common.domain.usecase.FlowMultiResultUseCase
import com.ttenushko.androidmvi.demo.common.domain.utils.asFlow
import com.ttenushko.androidmvi.demo.common.domain.weather.repository.WeatherForecastRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest

class SearchPlaceUseCaseImpl(
    private val weatherForecastRepository: WeatherForecastRepository,
    private val debounceMs: Long,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FlowMultiResultUseCase<SearchPlaceUseCase.Param, SearchPlaceUseCase.Result>(ioDispatcher),
    SearchPlaceUseCase {

    @Suppress("EXPERIMENTAL_API_USAGE")
    override fun createFlow(param: SearchPlaceUseCase.Param): Flow<SearchPlaceUseCase.Result> =
        param.search.asFlow()
            .debounce(debounceMs)
            .distinctUntilChanged()
            .mapLatest { search ->
                try {
                    val places =
                        if (search.isNotBlank()) weatherForecastRepository.findPlace(search)
                        else listOf()
                    SearchPlaceUseCase.Result.Success(search, places)
                } catch (error: Throwable) {
                    SearchPlaceUseCase.Result.Failure(search, error)
                }
            }
}