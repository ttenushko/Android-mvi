package com.ttenushko.androidmvi.demo.common.presentation.screen.places

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import com.ttenushko.androidmvi.demo.common.domain.application.usecase.TrackSavedPlacesUseCase
import com.ttenushko.androidmvi.demo.common.presentation.base.router.Router
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.common.presentation.screen.places.mvi.Store
import com.ttenushko.androidmvi.demo.common.presentation.utils.MviLogger
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.TaskExecutorFactory
import com.ttenushko.mvi.MviStore
import com.ttenushko.mvi.android.MviStoreHolderViewModel
import kotlinx.coroutines.Dispatchers

class PlacesViewModel(
    private val trackSavedPlacesUseCase: TrackSavedPlacesUseCase,
    private val taskExecutorFactory: TaskExecutorFactory,
    private val mviLogger: MviLogger<*, *>,
    private val router: Router<MainRouter.Destination>,
    savedStateHandle: SavedStateHandle,
) : MviStoreHolderViewModel<Store.Intent, Store.State, Store.Event>(savedStateHandle) {

    override fun createMviStore(savedState: Bundle?): MviStore<Store.Intent, Store.State, Store.Event> =
        Store.create(
            initialState = Store.State(null, null),
            trackSavedPlacesUseCase = trackSavedPlacesUseCase,
            taskExecutorFactory = taskExecutorFactory,
            mviLogger = mviLogger,
            router = router,
            coroutineDispatcher = Dispatchers.Main
        )

    override fun saveMviStoreState(state: Store.State): Bundle? =
        null
}