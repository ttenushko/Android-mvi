package com.ttenushko.androidmvi.demo.common.presentation.screen

import com.ttenushko.androidmvi.demo.common.presentation.base.router.Router

interface MainRouter : Router<MainRouter.Destination> {

    sealed class Destination : Router.Destination {
        object GoBack : Destination() {
            override fun toString(): String =
                "GoBack"
        }

        object Places : Destination() {
            override fun toString(): String =
                "Places"
        }

        data class AddPlace(val search: String) : Destination()

        data class PlaceDetails(val placeId: Long) : Destination()
    }
}