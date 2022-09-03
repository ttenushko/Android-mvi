package com.ttenushko.androidmvi.demo.presentation.screen

import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.savedstate.SavedStateRegistryOwner
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.presentation.base.router.ComposeRouter
import com.ttenushko.androidmvi.demo.presentation.base.router.Render
import com.ttenushko.androidmvi.demo.presentation.base.router.rememberScreen
import com.ttenushko.androidmvi.demo.presentation.utils.makeRoute
import com.ttenushko.androidmvi.demo.presentation.utils.parseRoute
import com.ttenushko.androidmvi.demo.presentation.utils.requireArguments
import com.ttenushko.mvi.android.compose.Screen

class MainRouterImpl(
    private val screenFactory: (SavedStateRegistryOwner, MainRouter.Destination) -> Screen,
) : ComposeRouter<MainRouter.Destination>(
    startDestination = ROUTE_PLACES
) {

    override fun buildNavGraph(
        navGraphBuilder: NavGraphBuilder,
    ) {
        with(navGraphBuilder) {
            composable(ROUTE_PLACES) {
                Render {
                    screenFactory.rememberScreen(LocalSavedStateRegistryOwner.current) { MainRouter.Destination.Places }
                }
            }
            composable(ROUTE_ADD_PLACE) {
                Render {
                    screenFactory.rememberScreen(LocalSavedStateRegistryOwner.current) {
                        MainRouter.Destination.AddPlace("")
                    }
                }
            }
            composable(
                route = parseRoute(ROUTE_PLACE_DETAILS, ARG_PLACE_ID),
                arguments = listOf(
                    navArgument(ARG_PLACE_ID) { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val args = backStackEntry.requireArguments()
                val placeId = args.getLong(ARG_PLACE_ID)
                Render {
                    screenFactory.rememberScreen(LocalSavedStateRegistryOwner.current, placeId) {
                        MainRouter.Destination.PlaceDetails(placeId)
                    }
                }
            }
        }
    }

    override fun navigate(navController: NavController, destination: MainRouter.Destination) {
        when (destination) {
            MainRouter.Destination.GoBack -> {
                navController.popBackStack()
            }
            MainRouter.Destination.Places -> {
                navController.navigate(ROUTE_PLACES) {
                    popUpTo(ROUTE_PLACES) {
                        inclusive = false
                    }
                }
            }
            is MainRouter.Destination.AddPlace -> {
                navController.navigate(ROUTE_ADD_PLACE)
            }
            is MainRouter.Destination.PlaceDetails -> {
                navController.navigate(
                    makeRoute(
                        ROUTE_PLACE_DETAILS,
                        ARG_PLACE_ID to destination.placeId.toString()
                    )
                )
            }
        }
    }

    companion object {
        private const val ARG_PLACE_ID = "placeId"

        private const val ROUTE_PLACES = "places"
        private const val ROUTE_ADD_PLACE = "addPlace"
        private const val ROUTE_PLACE_DETAILS = "placeDetails"
    }
}