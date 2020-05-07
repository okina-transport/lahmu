package org.entur.mobility.bikes

import java.time.LocalDateTime
import org.entur.mobility.bikes.bikeOperators.Operator

interface Cache {
    val cacheMap: HashMap<Operator, HashMap<GbfsStandardEnum, GBFSResponse>>
    val lastUpdated: LocalDateTime
}

class InMemoryCache(
    override val cacheMap: HashMap<Operator, HashMap<GbfsStandardEnum, GBFSResponse>>,
    override var lastUpdated: LocalDateTime
) : Cache {
    fun getResponseFromCache(bikeOperator: Operator, gbfsStandardEnum: GbfsStandardEnum): GBFSResponse? {
        return cacheMap[bikeOperator]?.get(gbfsStandardEnum)
    }

    fun setResponseInCacheAndGet(bikeOperator: Operator, gbfsStandardEnum: GbfsStandardEnum, response: GBFSResponse): GBFSResponse {
        if (cacheMap[bikeOperator] == null) {
            cacheMap[bikeOperator] = hashMapOf(gbfsStandardEnum to response)
        } else {
            cacheMap[bikeOperator]!![gbfsStandardEnum] = response
        }
        lastUpdated = LocalDateTime.now()
        return response
    }
    fun isValidCache(bikeOperator: Operator, gbfsStandardEnum: GbfsStandardEnum): Boolean =
        cacheMap[bikeOperator]?.get(gbfsStandardEnum) != null && lastUpdated > LocalDateTime.now().minusSeconds(60)
}
