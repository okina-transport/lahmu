package org.entur.lahmu.config

import io.ktor.client.HttpClient
import java.util.concurrent.ConcurrentHashMap
import org.entur.lahmu.legacy.service.BikeService
import org.entur.lahmu.legacy.service.BikeServiceImpl
import org.entur.lahmu.legacy.service.Cache
import org.entur.lahmu.legacy.service.InMemoryCache
import org.entur.lahmu.web.controllers.ProxyController
import org.entur.lahmu.web.controllers.ProxyControllerImpl
import org.koin.dsl.module

val modulesConfig = module {
    single<BikeService> { BikeServiceImpl(HttpClient()) }
    single<Cache> { InMemoryCache(ConcurrentHashMap()) }
    single<ProxyController> { (bikeService: BikeService, cache: Cache) -> ProxyControllerImpl(bikeService, cache) }
}
