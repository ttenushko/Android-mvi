package com.ttenushko.androidmvi.demo.di.presentation.screen

import androidx.appcompat.app.AppCompatActivity
import androidx.savedstate.SavedStateRegistryOwner
import com.ttenushko.androidmvi.demo.common.domain.application.usecase.DeletePlaceUseCase
import com.ttenushko.androidmvi.demo.common.domain.application.usecase.SavePlaceUseCase
import com.ttenushko.androidmvi.demo.common.domain.application.usecase.TrackSavedPlacesUseCase
import com.ttenushko.androidmvi.demo.common.domain.weather.usecase.GetCurrentWeatherConditionsUseCase
import com.ttenushko.androidmvi.demo.common.domain.weather.usecase.SearchPlaceUseCase
import com.ttenushko.androidmvi.demo.common.presentation.base.router.RouterProxy
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.AddPlaceViewModel
import com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.PlaceDetailsViewModel
import com.ttenushko.androidmvi.demo.common.presentation.screen.places.PlacesViewModel
import com.ttenushko.androidmvi.demo.common.presentation.utils.MviEventLogger
import com.ttenushko.androidmvi.demo.common.presentation.utils.MviLogger
import com.ttenushko.androidmvi.demo.common.presentation.utils.dagger.ActivityScope
import com.ttenushko.androidmvi.demo.common.presentation.utils.savedStateViewModelFactory
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.TaskExecutorFactory
import com.ttenushko.androidmvi.demo.presentation.screen.MainRouterImpl
import com.ttenushko.androidmvi.demo.presentation.screen.addplace.AddPlaceScreen
import com.ttenushko.androidmvi.demo.presentation.screen.placedetails.PlaceDetailsScreen
import com.ttenushko.androidmvi.demo.presentation.screen.places.PlacesScreen
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule(
    private val activity: AppCompatActivity,
) {
    @JvmSuppressWildcards
    @ActivityScope
    @Provides
    fun mainRouterImpl(
        placesScreenProvider: (SavedStateRegistryOwner) -> PlacesScreen,
        addPlaceScreenProvider: (SavedStateRegistryOwner) -> AddPlaceScreen,
        placeDetailsScreenProvider: (SavedStateRegistryOwner, Long) -> PlaceDetailsScreen,
    ): MainRouterImpl =
        MainRouterImpl { savedStateRegistryOwner, destination ->
            when (destination) {
                MainRouter.Destination.Places -> {
                    placesScreenProvider(savedStateRegistryOwner)
                }
                is MainRouter.Destination.AddPlace -> {
                    addPlaceScreenProvider(savedStateRegistryOwner)
                }
                is MainRouter.Destination.PlaceDetails -> {
                    placeDetailsScreenProvider(savedStateRegistryOwner, destination.placeId)
                }
                else -> throw IllegalStateException("Not implemented.")
            }
        }

    @Provides
    fun placesScreen(
        trackSavedPlacesUseCase: TrackSavedPlacesUseCase,
        taskExecutorFactory: TaskExecutorFactory,
        router: RouterProxy<MainRouter.Destination>,
    ): (SavedStateRegistryOwner) -> PlacesScreen = { savedStateRegistryOwner ->
        PlacesScreen(savedStateRegistryOwner.savedStateViewModelFactory { savedStateHandle ->
            PlacesViewModel(
                trackSavedPlacesUseCase,
                taskExecutorFactory,
                MviLogger<Any, Any>("Places"),
                router,
                savedStateHandle
            )
        })
    }

    @Provides
    fun addPlaceScreen(
        searchPlaceUseCase: SearchPlaceUseCase,
        savePlaceUseCase: SavePlaceUseCase,
        taskExecutorFactory: TaskExecutorFactory,
        router: RouterProxy<MainRouter.Destination>,
    ): (SavedStateRegistryOwner) -> AddPlaceScreen = { savedStateRegistryOwner ->
        AddPlaceScreen(savedStateRegistryOwner.savedStateViewModelFactory { savedStateHandle ->
            AddPlaceViewModel(
                searchPlaceUseCase,
                savePlaceUseCase,
                taskExecutorFactory,
                MviLogger<Any, Any>("AddPlace"),
                router,
                savedStateHandle
            )
        })
    }

    @Provides
    fun placeDetailsScreen(
        getCurrentWeatherConditionsUseCase: GetCurrentWeatherConditionsUseCase,
        deletePlaceUseCase: DeletePlaceUseCase,
        taskExecutorFactory: TaskExecutorFactory,
        router: RouterProxy<MainRouter.Destination>,
    ): (SavedStateRegistryOwner, Long) -> PlaceDetailsScreen =
        { savedStateRegistryOwner, placeId ->
            PlaceDetailsScreen(savedStateRegistryOwner.savedStateViewModelFactory { savedStateHandle ->
                PlaceDetailsViewModel(
                    placeId,
                    getCurrentWeatherConditionsUseCase,
                    deletePlaceUseCase,
                    taskExecutorFactory,
                    MviLogger<Any, Any>("PlaceDetails"),
                    MviEventLogger<Any>("PlaceDetails"),
                    router,
                    savedStateHandle
                )
            })
        }
}