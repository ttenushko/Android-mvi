package com.ttenushko.androidmvi.demo.presentation.screens.home

import android.app.Activity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ttenushko.androidmvi.demo.R
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.AddPlaceFragment
import com.ttenushko.androidmvi.demo.presentation.screens.home.placedetails.PlaceDetailsFragment
import com.ttenushko.androidmvi.demo.presentation.utils.getActionIdByDestinationId

class RouterImpl : Router {

    private lateinit var navController: NavController

    fun initialize(activity: Activity) {
        navController = Navigation.findNavController(activity, R.id.nav_host_fragment)
    }

    override fun navigateTo(destination: Router.Destination) {
        val currentDestination =
            navController.currentDestination?.id?.let { navController.graph.findNode(it) }
        when (destination) {
            is Router.Destination.GoBack -> {
                navController.popBackStack()
            }
            is Router.Destination.AddPlace -> {
                val destinationId = R.id.addPlaceFragment
                val actionId =
                    currentDestination?.getActionIdByDestinationId(destinationId) ?: destinationId
                navController.navigate(
                    actionId,
                    AddPlaceFragment.args(destination.search)
                )
            }
            is Router.Destination.PlaceDetails -> {
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