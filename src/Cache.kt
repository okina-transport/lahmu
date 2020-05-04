package org.entur

import java.time.LocalDateTime
import org.entur.BikeOperators.UrbanSharingOperator

interface Cache<T> {
    val cacheMap: HashMap<UrbanSharingOperator, T>
    val lastUpdated: LocalDateTime
}

class InMemoryCache<T>(
    override val cacheMap: HashMap<UrbanSharingOperator, T>,
    override var lastUpdated: LocalDateTime
) : Cache<T> {
    fun getResponseFromCache(bikeOperator: UrbanSharingOperator) =
        cacheMap[bikeOperator]

    fun setResponseInCache(bikeOperator: UrbanSharingOperator, response: T) {
        cacheMap[bikeOperator] = response
        lastUpdated = LocalDateTime.now()
    }
    fun isValidCache(bikeOperator: UrbanSharingOperator): Boolean =
        cacheMap[bikeOperator] != null && lastUpdated > LocalDateTime.now().minusSeconds(5)
}
