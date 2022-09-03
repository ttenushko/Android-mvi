package com.ttenushko.androidmvi.demo.di.presentation.screen

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.ttenushko.androidmvi.demo.common.di.Dependency
import com.ttenushko.androidmvi.demo.common.domain.application.usecase.DeletePlaceUseCase
import com.ttenushko.androidmvi.demo.common.domain.weather.usecase.GetCurrentWeatherConditionsUseCase
import com.ttenushko.androidmvi.demo.common.presentation.base.router.RouterProxy
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.PlaceDetailsViewModel
import com.ttenushko.androidmvi.demo.common.presentation.utils.MviEventLogger
import com.ttenushko.androidmvi.demo.common.presentation.utils.MviLogger
import com.ttenushko.androidmvi.demo.common.presentation.utils.savedStateViewModelFactory
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.TaskExecutorFactory
import com.ttenushko.androidmvi.demo.presentation.screens.placedetails.PlaceDetailsFragment
import dagger.Component
import dagger.Provides

interface PlaceDetailsFragmentDependencies : Dependency {
    fun getCurrentWeatherConditionsUseCase(): GetCurrentWeatherConditionsUseCase
    fun deletePlaceUseCase(): DeletePlaceUseCase
    fun picasso(): Picasso
    fun router(): RouterProxy<MainRouter.Destination>
    fun taskExecutorFactory(): TaskExecutorFactory
}

@dagger.Module
internal class PlaceDetailsFragmentModule(
    private val fragment: Fragment,
    private val placeId: Long,
) {
    @Provides
    fun viewModelFactory(
        getCurrentWeatherConditionsUseCase: GetCurrentWeatherConditionsUseCase,
        deletePlaceUseCase: DeletePlaceUseCase,
        taskExecutorFactory: TaskExecutorFactory,
        router: RouterProxy<MainRouter.Destination>,
    ): ViewModelProvider.Factory = fragment.savedStateViewModelFactory { savedStateHandle ->
        PlaceDetailsViewModel(
            placeId,
            getCurrentWeatherConditionsUseCase,
            deletePlaceUseCase,
            taskExecutorFactory,
            MviLogger<Any, Any>("PlaceDetailsFragment"),
            MviEventLogger<Any>("PlaceDetailsFragment"),
            router,
            savedStateHandle
        )
    }

    @Provides
    fun mviEventLogger(): MviEventLogger<Any> =
        MviEventLogger<Any>("PlaceDetailsFragment")
}

@Component(
    dependencies = [PlaceDetailsFragmentDependencies::class],
    modules = [PlaceDetailsFragmentModule::class]
)
internal interface PlaceDetailsFragmentComponent {

    @Component.Builder
    interface Builder {
        fun dependencies(dependencies: PlaceDetailsFragmentDependencies): Builder
        fun fragmentModule(fragmentModule: PlaceDetailsFragmentModule): Builder
        fun build(): PlaceDetailsFragmentComponent
    }

    fun inject(fragment: PlaceDetailsFragment)
}
