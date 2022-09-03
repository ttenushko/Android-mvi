package com.ttenushko.androidmvi.demo.di.presentation.screen

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ttenushko.androidmvi.demo.common.di.Dependency
import com.ttenushko.androidmvi.demo.common.domain.application.usecase.SavePlaceUseCase
import com.ttenushko.androidmvi.demo.common.domain.weather.usecase.SearchPlaceUseCase
import com.ttenushko.androidmvi.demo.common.presentation.base.router.RouterProxy
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.AddPlaceViewModel
import com.ttenushko.androidmvi.demo.common.presentation.utils.MviEventLogger
import com.ttenushko.androidmvi.demo.common.presentation.utils.MviLogger
import com.ttenushko.androidmvi.demo.common.presentation.utils.savedStateViewModelFactory
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.TaskExecutorFactory
import com.ttenushko.androidmvi.demo.presentation.screens.addplace.AddPlaceFragment
import dagger.Component
import dagger.Provides

interface AddPlaceFragmentDependencies : Dependency {
    fun searchPlaceUseCase(): SearchPlaceUseCase
    fun savePlaceUseCase(): SavePlaceUseCase
    fun router(): RouterProxy<MainRouter.Destination>
    fun taskExecutorFactory(): TaskExecutorFactory
}

@dagger.Module
internal class AddPlaceFragmentModule(
    private val fragment: Fragment,
    private val search: String,
) {
    @Provides
    fun viewModelFactory(
        searchPlaceUseCase: SearchPlaceUseCase,
        savePlaceUseCase: SavePlaceUseCase,
        taskExecutorFactory: TaskExecutorFactory,
        router: RouterProxy<MainRouter.Destination>,
    ): ViewModelProvider.Factory = fragment.savedStateViewModelFactory { savedStateHandle ->
        AddPlaceViewModel(
            searchPlaceUseCase,
            savePlaceUseCase,
            taskExecutorFactory,
            MviLogger<Any, Any>("AddPlaceFragment"),
            router,
            savedStateHandle
        )
    }

    @Provides
    fun mviEventLogger(): MviEventLogger<Any> =
        MviEventLogger<Any>("AddPlaceFragment")
}

@Component(
    dependencies = [AddPlaceFragmentDependencies::class],
    modules = [AddPlaceFragmentModule::class]
)
internal interface AddPlaceFragmentComponent {

    @Component.Builder
    interface Builder {
        fun dependencies(dependencies: AddPlaceFragmentDependencies): Builder
        fun fragmentModule(fragmentModule: AddPlaceFragmentModule): Builder
        fun build(): AddPlaceFragmentComponent
    }

    fun inject(fragment: AddPlaceFragment)
}
