package org.entur.lahmu.web

import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import org.entur.lahmu.domain.service.BikeService
import org.entur.lahmu.domain.service.Cache
import org.entur.lahmu.web.controllers.BikesController
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.inject

fun Routing.bikes() {
    val bikeService: BikeService by inject()
    val cache: Cache by inject()
    val bikesController: BikesController by inject { parametersOf(bikeService, cache) }

    route("") {
        get("/") {
            bikesController.getServiceDirectory(this.context)
        }

        get("{operator}/gbfs.json") {
            bikesController.getDiscoveryFeed(this.context)
        }

        get("{operator}/{service}.json") {
            bikesController.getGbfsFeed(this.context)
        }
    }

    route("bikes") {
        get("/") {
            bikesController.getServiceDirectory(this.context)
        }

        get("{operator}/gbfs.json") {
            bikesController.getDiscoveryFeed(this.context)
        }

        get("{operator}/{service}.json") {
            bikesController.getGbfsFeed(this.context)
        }
    }
}
