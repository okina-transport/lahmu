package org.entur.lahmu.legacy.service

import com.google.gson.reflect.TypeToken
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import java.lang.Exception
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.entur.lahmu.legacy.GbfsStandardEnum
import org.entur.lahmu.legacy.bikeOperators.JCDecauxResponse
import org.entur.lahmu.legacy.bikeOperators.JCDecauxStation
import org.entur.lahmu.legacy.bikeOperators.Operator
import org.entur.lahmu.legacy.bikeOperators.Operator.Companion.isJCDecaux
import org.entur.lahmu.legacy.bikeOperators.jcDecauxSystemInformation
import org.entur.lahmu.legacy.bikeOperators.jcDecauxSystemPricingPlans
import org.entur.lahmu.legacy.bikeOperators.lillestromBysykkelURL
import org.entur.lahmu.legacy.bikeOperators.toStationInformation
import org.entur.lahmu.legacy.bikeOperators.toStationStatus
import org.entur.lahmu.logger
import org.entur.lahmu.util.parseResponse

interface BikeService {
    val client: HttpClient

    fun poll(cache: Cache): Job
    fun fetchAndStoreInCache(
        cache: Cache,
        operator: Operator,
        gbfsStandardEnum: GbfsStandardEnum
    )
}

class BikeServiceImpl(override val client: HttpClient) : BikeService {

    override fun poll(cache: Cache) = GlobalScope.launch {
        Operator.values().forEach { operator ->
            launch {
                logger.info("Polling $operator")
                try {
                    fetchAndStoreInCache(cache, operator, GbfsStandardEnum.gbfs)
                } catch (e: Exception) {
                    logger.error("Failed to poll from $operator. $e")
                }
            }
        }
    }

    override fun fetchAndStoreInCache(
        cache: Cache,
        operator: Operator,
        gbfsStandardEnum: GbfsStandardEnum
    ) {
        if (operator.isJCDecaux()) {
            cache.setResponseInCacheAndGet(
                operator,
                GbfsStandardEnum.system_pricing_plans,
                jcDecauxSystemPricingPlans()
            )
            cache.setResponseInCacheAndGet(
                operator,
                GbfsStandardEnum.system_information,
                jcDecauxSystemInformation()
            )
            val response = JCDecauxResponse(data = parseJCDecauxResponse())
            cache.setResponseInCacheAndGet(
                operator,
                GbfsStandardEnum.station_status,
                response.toStationStatus()
            )
            cache.setResponseInCacheAndGet(
                operator,
                GbfsStandardEnum.station_information,
                response.toStationInformation()
            )
        }
    }

    private fun parseJCDecauxResponse(): List<JCDecauxStation> {
        val response = fetch(lillestromBysykkelURL.getValue(GbfsStandardEnum.system_information))
        val itemType = object : TypeToken<List<JCDecauxStation>>() {}.type
        return parseResponse(response, itemType)
    }

    private fun fetch(url: String): String {
        val response = runBlocking { client.get<String>(url) { header("Client-Identifier", "entur-bikeservice") } }
        return response
    }
}
