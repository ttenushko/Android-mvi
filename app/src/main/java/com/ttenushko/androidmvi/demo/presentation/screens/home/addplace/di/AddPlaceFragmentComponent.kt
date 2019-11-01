package com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ttenushko.androidmvi.demo.di.dependency.ComponentDependencies
import com.ttenushko.androidmvi.demo.domain.application.usecase.SavePlaceUseCase
import com.ttenushko.androidmvi.demo.domain.weather.usecase.SearchPlaceUseCase
import com.ttenushko.androidmvi.demo.presentation.di.annotation.ViewModelKey
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.AddPlaceFragment
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.AddPlacesFragmentViewModel
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.Action
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.AddPlaceStore
import com.ttenushko.androidmvi.demo.presentation.utils.MviEventLogger
import com.ttenushko.androidmvi.demo.presentation.utils.MviLogger
import com.ttenushko.androidmvi.demo.presentation.utils.ViewModelFactory
import dagger.Binds
import dagger.Component
import dagger.Provides
import dagger.multibindings.IntoMap

interface AddPlaceFragmentDependencies : ComponentDependencies {
    fun mviEventLogger(): MviEventLogger<Any>
    fun mviLogger(): MviLogger<Any, Any>
    fun searchPlaceUseCase(): SearchPlaceUseCase
    fun savePlaceUseCase(): SavePlaceUseCase
}

@dagger.Module
internal class AddPlaceFragmentModule(private val search: String) {
    @Suppress("UNCHECKED_CAST")
    @Provides
    fun provideViewModel(
        mviLogger: MviLogger<Any, Any>,
        searchPlaceUseCase: SearchPlaceUseCase,
        savePlaceUseCase: SavePlaceUseCase
    ): AddPlacesFragmentViewModel =
        AddPlacesFragmentViewModel(
            mviLogger as MviLogger<Action, AddPlaceStore.State>,
            search,
            searchPlaceUseCase,
            savePlaceUseCase
        )
}

@dagger.Module
internal abstract class ViewModelBindingModule {
    @Binds
    @IntoMap
    @ViewModelKey(AddPlacesFragmentViewModel::class)
    internal abstract fun bindViewModel(viewModel: AddPlacesFragmentViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@Component(
    dependencies = [AddPlaceFragmentDependencies::class],
    modules = [AddPlaceFragmentModule::class, ViewModelBindingModule::class]
)
internal interface AddPlaceFragmentComponent {

    @Component.Builder
    interface Builder {
        fun addPlaceFragmentDependencies(addPlaceFragmentDependencies: AddPlaceFragmentDependencies): Builder
        fun addPlaceFragmentModule(addPlaceFragmentModule: AddPlaceFragmentModule): Builder
        fun build(): AddPlaceFragmentComponent
    }

    fun inject(fragment: AddPlaceFragment)
}
