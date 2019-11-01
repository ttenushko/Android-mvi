package com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.ttenushko.androidmvi.demo.di.dependency.ComponentDependencies
import com.ttenushko.androidmvi.demo.domain.application.usecase.DeletePlaceUseCase
import com.ttenushko.androidmvi.demo.domain.weather.usecase.GetCurrentWeatherConditionsUseCase
import com.ttenushko.androidmvi.demo.presentation.di.annotation.ViewModelKey
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.PlaceDetailsFragment
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.PlaceDetailsFragmentViewModel
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.Action
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.mvi.PlaceDetailsStore
import com.ttenushko.androidmvi.demo.presentation.utils.MviEventLogger
import com.ttenushko.androidmvi.demo.presentation.utils.MviLogger
import com.ttenushko.androidmvi.demo.presentation.utils.ViewModelFactory
import dagger.Binds
import dagger.Component
import dagger.Provides
import dagger.multibindings.IntoMap

interface PlaceDetailsFragmentDependencies : ComponentDependencies {
    fun mviEventLogger(): MviEventLogger<Any>
    fun mviLogger(): MviLogger<Any, Any>
    fun getCurrentWeatherConditionsUseCase(): GetCurrentWeatherConditionsUseCase
    fun deletePlaceUseCase(): DeletePlaceUseCase
    fun picasso(): Picasso
}

@dagger.Module
internal class PlaceDetailsFragmentModule(private val placeId: Long) {
    @Suppress("UNCHECKED_CAST")
    @Provides
    fun provideViewModel(
        mviLogger: MviLogger<Any, Any>,
        getCurrentWeatherConditionsUseCase: GetCurrentWeatherConditionsUseCase,
        deletePlaceUseCase: DeletePlaceUseCase
    ): PlaceDetailsFragmentViewModel =
        PlaceDetailsFragmentViewModel(
            mviLogger as MviLogger<Action, PlaceDetailsStore.State>,
            placeId,
            getCurrentWeatherConditionsUseCase,
            deletePlaceUseCase
        )
}

@dagger.Module
internal abstract class ViewModelBindingModule {
    @Binds
    @IntoMap
    @ViewModelKey(PlaceDetailsFragmentViewModel::class)
    internal abstract fun bindViewModel(viewModel: PlaceDetailsFragmentViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}


@Component(
    dependencies = [PlaceDetailsFragmentDependencies::class],
    modules = [PlaceDetailsFragmentModule::class, ViewModelBindingModule::class]
)
internal interface PlaceDetailsFragmentComponent {

    @Component.Builder
    interface Builder {
        fun placeDetailsFragmentDependencies(placeDetailsFragmentDependencies: PlaceDetailsFragmentDependencies): Builder
        fun placeDetailsFragmentModule(placeDetailsFragmentModule: PlaceDetailsFragmentModule): Builder
        fun build(): PlaceDetailsFragmentComponent
    }

    fun inject(fragment: PlaceDetailsFragment)
}
