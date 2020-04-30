package org.entur

import java.time.LocalDateTime

interface Cache {
    val cacheMap: HashMap<BikeOperator, BikeResponse>
    val lastUpdated: LocalDateTime
}

class InMemoryCache(
    override val cacheMap: HashMap<BikeOperator, BikeResponse>,
    override var lastUpdated: LocalDateTime
) : Cache {
    fun getResponseFromCache(bikeOperator: BikeOperator) =
        cacheMap[bikeOperator]

    fun setResponseInCache(bikeOperator: BikeOperator, response: BikeResponse) {
        println("$bikeOperator - Setting response in cache")
        cacheMap[bikeOperator] = response
        lastUpdated = LocalDateTime.now()
    }
    fun isValidCache(bikeOperator: BikeOperator): Boolean =
        cacheMap[bikeOperator] != null && lastUpdated > LocalDateTime.now().minusSeconds(5)
}
