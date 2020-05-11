package org.entur.mobility.bikes

import java.time.LocalDateTime
import java.time.ZoneOffset
import org.entur.mobility.bikes.bikeOperators.Operator

interface Cache {
    val cacheMap: HashMap<Operator, HashMap<GbfsStandardEnum, GBFSResponse>>
}

class InMemoryCache(
    override val cacheMap: HashMap<Operator, HashMap<GbfsStandardEnum, GBFSResponse>>
) : Cache {
    fun getResponseFromCache(bikeOperator: Operator, gbfsStandardEnum: GbfsStandardEnum): GBFSResponse? {
        return cacheMap[bikeOperator]?.get(gbfsStandardEnum)
    }

    fun setResponseInCacheAndGet(
        operator: Operator,
        gbfsStandardEnum: GbfsStandardEnum,
        response: GBFSResponse
    ): GBFSResponse {
        if (cacheMap[operator] == null) {
            cacheMap[operator] = hashMapOf(gbfsStandardEnum to response)
        } else {
            cacheMap[operator]!![gbfsStandardEnum] = response
        }
        return response
    }

    fun isValidCache(bikeOperator: Operator, gbfsStandardEnum: GbfsStandardEnum): Boolean =
        LocalDateTime.ofEpochSecond(
            cacheMap[bikeOperator]?.get(gbfsStandardEnum)?.last_updated ?: 0L,
            0,
            ZoneOffset.UTC
        ) > LocalDateTime.now()
            .minusSeconds(TIME_TO_LIVE_CACHE_SEC)
}
