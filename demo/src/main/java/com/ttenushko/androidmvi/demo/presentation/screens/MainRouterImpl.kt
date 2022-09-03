package com.ttenushko.androidmvi.demo.presentation.screens

import android.app.Activity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ttenushko.androidmvi.demo.R
import com.ttenushko.androidmvi.demo.common.presentation.base.router.Router
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.presentation.screens.addplace.AddPlaceFragment
import com.ttenushko.androidmvi.demo.presentation.screens.placedetails.PlaceDetailsFragment
import com.ttenushko.androidmvi.demo.presentation.utils.getActionIdByDestinationId

class MainRouterImpl(
    activity: Activity,
) : Router<MainRouter.Destination> {

    private val navController: NavController by lazy {
        Navigation.findNavController(activity, R.id.nav_host_fragment)
    }

    override fun navigateTo(destination: MainRouter.Destination) {
        val currentDestination =
            navController.currentDestination?.id?.let { navController.graph.findNode(it) }
        when (destination) {
            is MainRouter.Destination.GoBack -> {
                navController.popBackStack()
            }
            is MainRouter.Destination.Places -> {
                // do nothing
            }
            is MainRouter.Destination.AddPlace -> {
                val destinationId = R.id.addPlaceFragment
                val actionId =
                    currentDestination?.getActionIdByDestinationId(destinationId) ?: destinationId
                navController.navigate(
                    actionId,
                    AddPlaceFragment.args(destination.search)
                )
            }
            is MainRouter.Destination.PlaceDetails -> {
                val destinationId = R.id.placeDetailsFragment
                val actionId =
                    currentDestination?.getActionIdByDestinationId(destinationId) ?: destinationId
                navController.navigate(
                    actionId,
                    PlaceDetailsFragment.args(destination.placeId)
                )
            }
        }
    }
}