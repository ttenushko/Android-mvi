package com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi

import com.ttenushko.androidmvi.MviMiddleware
import com.ttenushko.androidmvi.MviMiddlewareImpl
import com.ttenushko.androidmvi.demo.domain.utils.Either
import com.ttenushko.androidmvi.demo.domain.weather.usecase.GetCurrentWeatherConditionsUseCase
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.PlaceDetailsStore.Event
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.PlaceDetailsStore.State
import com.ttenushko.androidmvi.demo.presentation.utils.task.SingleResultTask
import com.ttenushko.androidmvi.demo.presentation.utils.task.asSingleResultJob
import kotlinx.coroutines.Dispatchers

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
                        GetCurrentWeatherConditionsUseCase.Param(chain.stateProvider.get().placeId),
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
        SingleResultTask<GetCurrentWeatherConditionsUseCase.Param, GetCurrentWeatherConditionsUseCase.Result, Unit>(
            "getCurrentWeatherConditions",
            Dispatchers.Main,
            { param, tag ->
                actionDispatcher.dispatch(Action.Reloading)
                getCurrentWeatherConditionsUseCase.asSingleResultJob(param, tag)
            },
            { result, _ ->
                actionDispatcher.dispatch(Action.Reloaded(Either.Right(result.weather)))
            },
            { error, _ ->
                actionDispatcher.dispatch(Action.Reloaded(Either.Left(error)))
            }
        )
}