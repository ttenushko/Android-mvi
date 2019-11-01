package com.ttenushko.androidmvi.demo.presentation.screens.home.places.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ttenushko.androidmvi.demo.di.dependency.ComponentDependencies
import com.ttenushko.androidmvi.demo.domain.application.usecase.TrackSavedPlacesUseCase
import com.ttenushko.androidmvi.demo.presentation.di.annotation.ViewModelKey
import com.ttenushko.androidmvi.demo.presentation.screens.home.places.PlacesFragment
import com.ttenushko.androidmvi.demo.presentation.screens.home.places.PlacesFragmentViewModel
import com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi.Action
import com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi.PlacesStore
import com.ttenushko.androidmvi.demo.presentation.utils.MviEventLogger
import com.ttenushko.androidmvi.demo.presentation.utils.MviLogger
import com.ttenushko.androidmvi.demo.presentation.utils.ViewModelFactory
import dagger.Binds
import dagger.Component
import dagger.Provides
import dagger.multibindings.IntoMap

interface PlacesFragmentDependencies : ComponentDependencies {
    fun mviEventLogger(): MviEventLogger<Any>
    fun mviLogger(): MviLogger<Any, Any>
    fun trackSavedPlacesUseCase(): TrackSavedPlacesUseCase
}

@dagger.Module
internal class PlacesFragmentModule {
    @Suppress("UNCHECKED_CAST")
    @Provides
    fun provideViewModel(
        mviLogger: MviLogger<Any, Any>,
        trackSavedPlacesUseCase: TrackSavedPlacesUseCase
    ): PlacesFragmentViewModel =
        PlacesFragmentViewModel(
            mviLogger as MviLogger<Action, PlacesStore.State>,
            trackSavedPlacesUseCase
        )
}

@dagger.Module
internal abstract class ViewModelBindingModule {
    @Binds
    @IntoMap
    @ViewModelKey(PlacesFragmentViewModel::class)
    internal abstract fun bindViewModel(viewModel: PlacesFragmentViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@Component(
    dependencies = [PlacesFragmentDependencies::class],
    modules = [PlacesFragmentModule::class, ViewModelBindingModule::class]
)
internal interface PlacesFragmentComponent {
    fun inject(fragment: PlacesFragment)
}

