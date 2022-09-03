package com.ttenushko.androidmvi.demo.di.presentation.screen

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ttenushko.androidmvi.demo.common.di.Dependency
import com.ttenushko.androidmvi.demo.common.domain.application.usecase.TrackSavedPlacesUseCase
import com.ttenushko.androidmvi.demo.common.presentation.base.router.Router
import com.ttenushko.androidmvi.demo.common.presentation.base.router.RouterProxy
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.common.presentation.screen.places.PlacesViewModel
import com.ttenushko.androidmvi.demo.common.presentation.utils.MviEventLogger
import com.ttenushko.androidmvi.demo.common.presentation.utils.MviLogger
import com.ttenushko.androidmvi.demo.common.presentation.utils.savedStateViewModelFactory
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.TaskExecutorFactory
import com.ttenushko.androidmvi.demo.presentation.screens.places.PlacesFragment
import dagger.Component
import dagger.Provides

interface PlacesFragmentDependencies : Dependency {
    fun trackSavedPlacesUseCase(): TrackSavedPlacesUseCase
    fun router(): RouterProxy<MainRouter.Destination>
    fun taskExecutorFactory(): TaskExecutorFactory
}

@dagger.Module
internal class PlacesFragmentModule(
    private val fragment: Fragment,
) {
    @Provides
    fun viewModelFactory(
        trackSavedPlacesUseCase: TrackSavedPlacesUseCase,
        taskExecutorFactory: TaskExecutorFactory,
        router: RouterProxy<MainRouter.Destination>,
    ): ViewModelProvider.Factory = fragment.savedStateViewModelFactory { savedStateHandle ->
        PlacesViewModel(
            trackSavedPlacesUseCase,
            taskExecutorFactory,
            MviLogger<Any, Any>("PlacesFragment"),
            router,
            savedStateHandle
        )
    }

    @Provides
    fun mviEventLogger(): MviEventLogger<Any> =
        MviEventLogger<Any>("PlacesFragment")
}

@Component(
    dependencies = [PlacesFragmentDependencies::class],
    modules = [PlacesFragmentModule::class]
)
internal interface PlacesFragmentComponent {

    @Component.Builder
    interface Builder {
        fun dependencies(dependencies: PlacesFragmentDependencies): Builder
        fun fragmentModule(module: PlacesFragmentModule): Builder
        fun build(): PlacesFragmentComponent
    }

    fun inject(fragment: PlacesFragment)
}

