package com.ttenushko.androidmvi.demo.common.presentation.screen.addplace

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import com.ttenushko.androidmvi.demo.common.domain.application.usecase.SavePlaceUseCase
import com.ttenushko.androidmvi.demo.common.domain.weather.usecase.SearchPlaceUseCase
import com.ttenushko.androidmvi.demo.common.presentation.base.router.Router
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi.Store
import com.ttenushko.androidmvi.demo.common.presentation.utils.MviLogger
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.TaskExecutorFactory
import com.ttenushko.mvi.MviStore
import com.ttenushko.mvi.android.MviStoreHolderViewModel
import kotlinx.coroutines.Dispatchers

class AddPlaceViewModel(
    private val searchPlaceUseCase: SearchPlaceUseCase,
    private val savePlaceUseCase: SavePlaceUseCase,
    private val taskExecutorFactory: TaskExecutorFactory,
    private val mviLogger: MviLogger<*, *>,
    private val router: Router<MainRouter.Destination>,
    savedStateHandle: SavedStateHandle,
) : MviStoreHolderViewModel<Store.Intent, Store.State, Store.Event>(savedStateHandle) {

    override fun createMviStore(savedState: Bundle?): MviStore<Store.Intent, Store.State, Store.Event> =
        Store.create(
            initialState = Store.State((savedState?.get(SEARCH) as? String) ?: "", null),
            searchPlaceUseCase = searchPlaceUseCase,
            savePlaceUseCase = savePlaceUseCase,
            taskExecutorFactory = taskExecutorFactory,
            mviLogger = mviLogger,
            router = router,
            coroutineDispatcher = Dispatchers.Main
        )

    override fun saveMviStoreState(state: Store.State): Bundle =
        Bundle().apply {
            putString(SEARCH, state.search)
        }

    companion object {
        private const val SEARCH = "search"
    }
}