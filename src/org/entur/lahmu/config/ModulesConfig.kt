package org.entur.lahmu.config

import io.ktor.client.HttpClient
import org.entur.lahmu.legacy.service.BikeService
import org.entur.lahmu.legacy.service.BikeServiceImpl
import org.entur.lahmu.legacy.service.Cache
import org.entur.lahmu.legacy.service.InMemoryCache
import org.entur.lahmu.web.controllers.BikesController
import org.entur.lahmu.web.controllers.BikesControllerImpl
import org.entur.lahmu.web.controllers.ProxyController
import org.entur.lahmu.web.controllers.ProxyControllerImpl
import org.koin.dsl.module

val modulesConfig = module {
    single<BikeService> { BikeServiceImpl(HttpClient()) }
    single<Cache> { InMemoryCache(HashMap()) }
    single<BikesController> { (bikeService: BikeService, cache: Cache) -> BikesControllerImpl(bikeService, cache) }
    single<ProxyController> { (bikeService: BikeService, cache: Cache) -> ProxyControllerImpl(bikeService, cache) }
}
