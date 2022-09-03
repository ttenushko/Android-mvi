package com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import com.ttenushko.androidmvi.demo.common.domain.application.usecase.DeletePlaceUseCase
import com.ttenushko.androidmvi.demo.common.domain.weather.usecase.GetCurrentWeatherConditionsUseCase
import com.ttenushko.androidmvi.demo.common.presentation.base.router.Router
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.mvi.Store
import com.ttenushko.androidmvi.demo.common.presentation.utils.MviEventLogger
import com.ttenushko.androidmvi.demo.common.presentation.utils.MviLogger
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.TaskExecutorFactory
import com.ttenushko.mvi.MviStore
import com.ttenushko.mvi.android.MviStoreHolderViewModel
import kotlinx.coroutines.Dispatchers

class PlaceDetailsViewModel(
    private val placeId: Long,
    private val getCurrentWeatherConditionsUseCase: GetCurrentWeatherConditionsUseCase,
    private val deletePlaceUseCase: DeletePlaceUseCase,
    private val taskExecutorFactory: TaskExecutorFactory,
    private val mviLogger: MviLogger<*, *>,
    private val mviEventLogger: MviEventLogger<*>,
    private val router: Router<MainRouter.Destination>,
    savedStateHandle: SavedStateHandle,
) : MviStoreHolderViewModel<Store.Intent, Store.State, Store.Event>(savedStateHandle) {

    override fun createMviStore(savedState: Bundle?): MviStore<Store.Intent, Store.State, Store.Event> =
        Store.create(
            initialState = Store.State(
                placeId,
                weather = null,
                error = null,
                isRefreshing = false,
                isDeleting = false
            ),
            getCurrentWeatherConditionsUseCase = getCurrentWeatherConditionsUseCase,
            deletePlaceUseCase = deletePlaceUseCase,
            taskExecutorFactory = taskExecutorFactory,
            mviLogger = mviLogger,
            router = router,
            coroutineDispatcher = Dispatchers.Main
        ).also {
            it.addEventListener(mviEventLogger as MviEventLogger<Store.Event>)
        }

    override fun saveMviStoreState(state: Store.State): Bundle? =
        null
}