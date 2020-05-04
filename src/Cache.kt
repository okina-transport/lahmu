package org.entur

import java.time.LocalDateTime

interface Cache<T> {
    val cacheMap: HashMap<BikeOperator, T>
    val lastUpdated: LocalDateTime
}

class InMemoryCache<T>(
    override val cacheMap: HashMap<BikeOperator, T>,
    override var lastUpdated: LocalDateTime
) : Cache<T> {
    fun getResponseFromCache(bikeOperator: BikeOperator) =
        cacheMap[bikeOperator]

    fun setResponseInCache(bikeOperator: BikeOperator, response: T) {
        cacheMap[bikeOperator] = response
        lastUpdated = LocalDateTime.now()
    }
    fun isValidCache(bikeOperator: BikeOperator): Boolean =
        cacheMap[bikeOperator] != null && lastUpdated > LocalDateTime.now().minusSeconds(5)
}
