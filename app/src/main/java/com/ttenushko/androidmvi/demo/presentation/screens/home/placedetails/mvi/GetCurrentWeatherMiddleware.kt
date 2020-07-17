package com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi

import com.ttenushko.androidmvi.demo.domain.utils.Either
import com.ttenushko.androidmvi.demo.domain.weather.usecase.GetCurrentWeatherConditionsUseCase
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.PlaceDetailsStore.Event
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.PlaceDetailsStore.State
import com.ttenushko.androidmvi.demo.presentation.utils.usecase.createExecutor
import com.ttenushko.androidmvi.demo.presentation.utils.usecase.singleResultUseCaseProvider
import com.ttenushko.mvi.MviMiddleware
import com.ttenushko.mvi.MviMiddlewareImpl

internal class GetCurrentWeatherMiddleware(
    private val getCurrentWeatherConditionsUseCase: GetCurrentWeatherConditionsUseCase
) : MviMiddlewareImpl<Action, State, Event>() {

    private val taskGetCurrentWeather = createGetCurrentWeatherTask()

    override fun apply(chain: MviMiddleware.Chain<Action, State, Event>) {
        super.apply(chain)
        when (chain.action) {
            is Action.Reload -> {
                if (!taskGetCurrentWeather.isRunning) {
                    taskGetCurrentWeather.start(
                        GetCurrentWeatherConditionsUseCase.Param(chain.state.placeId),
                        Unit
                    )
                }
            }
            else -> chain.proceed()
        }
    }

    override fun onClose() {
        taskGetCurrentWeather.stop()
    }

    private fun createGetCurrentWeatherTask() =
        createExecutor<GetCurrentWeatherConditionsUseCase.Param, GetCurrentWeatherConditionsUseCase.Result, Unit>(
            singleResultUseCaseProvider { _, _ -> getCurrentWeatherConditionsUseCase },
            { result, _ ->
                actionDispatcher.dispatch(Action.Reloaded(Either.Right(result.weather)))
            },
            { error, _ ->
                actionDispatcher.dispatch(Action.Reloaded(Either.Left(error)))
            }
        )
}