package org.entur.lahmu.legacy.service

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import org.entur.lahmu.config.TTL
import org.entur.lahmu.legacy.GBFSResponse
import org.entur.lahmu.legacy.GbfsStandardEnum
import org.entur.lahmu.legacy.bikeOperators.Operator

interface Cache {
    val cacheMap: Map<Operator, Map<GbfsStandardEnum, GBFSResponse>>

    fun isValidCache(bikeOperator: Operator, gbfsStandardEnum: GbfsStandardEnum): Boolean
    fun getResponseFromCache(bikeOperator: Operator, gbfsStandardEnum: GbfsStandardEnum): GBFSResponse?
    fun setResponseInCacheAndGet(operator: Operator, gbfsStandardEnum: GbfsStandardEnum, response: GBFSResponse): GBFSResponse
}

class InMemoryCache(
    override val cacheMap: ConcurrentMap<Operator, ConcurrentMap<GbfsStandardEnum, GBFSResponse>>
) : Cache {
    override fun getResponseFromCache(bikeOperator: Operator, gbfsStandardEnum: GbfsStandardEnum): GBFSResponse? {
        return cacheMap[bikeOperator]?.get(gbfsStandardEnum)
    }

    override fun setResponseInCacheAndGet(
        operator: Operator,
        gbfsStandardEnum: GbfsStandardEnum,
        response: GBFSResponse
    ): GBFSResponse {
        if (cacheMap[operator] == null) {
            val map = ConcurrentHashMap<GbfsStandardEnum, GBFSResponse>()
            map[gbfsStandardEnum] = response
            cacheMap[operator] = map
        } else {
            cacheMap[operator]!![gbfsStandardEnum] = response
        }
        return response
    }

    override fun isValidCache(bikeOperator: Operator, gbfsStandardEnum: GbfsStandardEnum): Boolean {
        return cacheCheck(cacheMap, bikeOperator, gbfsStandardEnum)
    }
}

fun cacheCheck(
    cacheMap: Map<Operator, Map<GbfsStandardEnum, GBFSResponse>>,
    bikeOperator: Operator,
    gbfsStandardEnum: GbfsStandardEnum
) =
    LocalDateTime.ofEpochSecond(
        cacheMap[bikeOperator]?.get(gbfsStandardEnum)?.lastUpdated ?: 0L,
        0,
        ZoneOffset.UTC
    ) > LocalDateTime.now().minusSeconds(TTL)
