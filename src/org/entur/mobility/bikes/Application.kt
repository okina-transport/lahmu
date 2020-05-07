package org.entur.mobility.bikes

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import java.time.LocalDateTime
import kotlin.concurrent.thread
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.entur.mobility.bikes.GbfsStandardEnum.Companion.getFetchUrl
import org.entur.mobility.bikes.bikeOperators.KolumbusResponse
import org.entur.mobility.bikes.bikeOperators.KolumbusStation
import org.entur.mobility.bikes.bikeOperators.Operator
import org.entur.mobility.bikes.bikeOperators.Operator.Companion.isUrbanSharing
import org.entur.mobility.bikes.bikeOperators.getOperatorsWithDiscovery
import org.entur.mobility.bikes.bikeOperators.toStationInformation
import org.entur.mobility.bikes.bikeOperators.toStationStatus
import org.entur.mobility.bikes.bikeOperators.toSystemInformation

const val POLL_INTERVAL = 60000L

fun main() {
    val server = embeddedServer(Jetty, watchPaths = listOf("bikeservice"), port = 8080, module = Application::module)
    server.start(wait = true)
}

fun Application.module() {
    val cache = InMemoryCache(HashMap(), LocalDateTime.now())

    thread(start = true) {
        launch { poll(cache) }
    }

    routing {
        get("/") {
            val host = call.request.host()
            val port = call.request.port()
            call.respondText(Gson().toJson(getOperatorsWithDiscovery(host, port)), ContentType.Application.Json)
        }

        get("/health") {
            call.respondText("OK")
        }

        get("{operator}/gbfs.json") {
            val operator = Operator.valueOf(call.parameters["operator"]?.toUpperCase() ?: throw NullPointerException())
            val gbfsEndpoints = getGbfsEndpoint(operator, call.request.host(), call.request.port())
            val response = getDiscovery(gbfsEndpoints)
            call.respondText(Gson().toJson(response), ContentType.Application.Json)
        }

        get("{operator}/system_information.json") {
            val operator = Operator.valueOf(call.parameters["operator"]?.toUpperCase() ?: throw NullPointerException())
            val gbfsEnum = GbfsStandardEnum.valueOf("system_information")
            val result = when {
                cache.isValidCache(operator, GbfsStandardEnum.system_information) -> cache.getResponseFromCache(operator, GbfsStandardEnum.system_information)
                operator === Operator.KOLUMBUSBYSYKKEL -> {
                    val response =
                        KolumbusResponse(
                            data = parseKolumbusResponse(gbfsEnum.getFetchUrl(operator)!!)
                        ).toSystemInformation()
                    cache.setResponseInCacheAndGet(operator, GbfsStandardEnum.station_information, response)
                }
                else -> {
                    val response = parseResponse<GBFSResponse.SystemInformationResponse>(
                        gbfsEnum.getFetchUrl(operator)!!
                    )
                    cache.setResponseInCacheAndGet(operator, GbfsStandardEnum.station_information, response)
                }
            }
            call.respondText(Gson().toJson(result), ContentType.Application.Json)
        }

        get("{operator}/station_information.json") {
            val operator = Operator.valueOf(call.parameters["operator"]?.toUpperCase() ?: throw NullPointerException())
            val gbfsEnum = GbfsStandardEnum.valueOf("station_information")
            val result = when {
                (cache.isValidCache(operator, GbfsStandardEnum.station_information)) -> cache.getResponseFromCache(
                    operator,
                    GbfsStandardEnum.station_information
                )
                operator === Operator.KOLUMBUSBYSYKKEL -> {
                    val response = KolumbusResponse(
                        data = parseKolumbusResponse(
                            gbfsEnum.getFetchUrl(operator)!!
                        )
                    )
                        .toStationInformation().toNeTEx(operator)
                    cache.setResponseInCacheAndGet(operator, GbfsStandardEnum.station_information, response)
                }
                else -> {
                    val response = parseResponse<GBFSResponse.StationsResponse>(
                        gbfsEnum.getFetchUrl(operator)!!
                    ).toNeTEx(operator)
                    cache.setResponseInCacheAndGet(operator, GbfsStandardEnum.station_information, response)
                }
            }
            call.respondText(Gson().toJson(result), ContentType.Application.Json)
        }

        get("{operator}/station_status.json") {
            val operator = Operator.valueOf(call.parameters["operator"]?.toUpperCase() ?: throw NullPointerException())
            val gbfsEnum = GbfsStandardEnum.valueOf("station_status")
            val result = when {
                (cache.isValidCache(operator, GbfsStandardEnum.station_status)) -> cache.getResponseFromCache(operator, GbfsStandardEnum.station_status)
                operator === Operator.KOLUMBUSBYSYKKEL -> {
                    val response = KolumbusResponse(
                        data = parseKolumbusResponse(
                            gbfsEnum.getFetchUrl(operator)!!
                        )
                    )
                        .toStationStatus().toNeTEx(operator)
                    cache.setResponseInCacheAndGet(operator, GbfsStandardEnum.station_status, response)
                }
                else -> {
                    val response = parseResponse<GBFSResponse.StationStatusesResponse>(
                        gbfsEnum.getFetchUrl(operator)!!
                    ).toNeTEx(operator)
                    cache.setResponseInCacheAndGet(operator, GbfsStandardEnum.station_status, response)
                }
            }
            call.respondText(Gson().toJson(result), ContentType.Application.Json)
        }
    }
}

suspend inline fun <reified T> parseResponse(url: String): T {
    with(HttpClient()) {
        val response = get<String>(url) { header("Client-Identifier", "entur-bikeservice") }
        return Gson().fromJson(response, T::class.java)
    }
}

suspend inline fun parseKolumbusResponse(url: String): List<KolumbusStation> {
    with(HttpClient()) {
        val response = get<String>(url) { header("Client-Identifier", "entur-bikeservice") }
        val itemType = object : TypeToken<List<KolumbusStation>>() {}.type
        return Gson().fromJson(response, itemType)
    }
}

suspend inline fun poll(cache: InMemoryCache) {
    while (true) {
        Operator.values().forEach { operator ->
            GbfsStandardEnum.values().forEach {
                if (operator.isUrbanSharing()) {
                    when (it) {
                        GbfsStandardEnum.gbfs -> {}
                        GbfsStandardEnum.system_information -> {
                            val response = parseResponse<GBFSResponse.SystemInformationResponse>(it.getFetchUrl(operator)!!)
                    cache.setResponseInCacheAndGet(operator, it, response)
                        }
                        GbfsStandardEnum.station_information -> {
                            val response = parseResponse<GBFSResponse.StationsResponse>(it.getFetchUrl(operator)!!)
                    cache.setResponseInCacheAndGet(operator, it, response)
                        }
                        GbfsStandardEnum.station_status -> {
                            val response = parseResponse<GBFSResponse.StationStatusesResponse>(it.getFetchUrl(operator)!!)
                    cache.setResponseInCacheAndGet(operator, it, response)
                        }
                    }
                } else {
                    when (it) {
                        GbfsStandardEnum.gbfs -> {}
                        GbfsStandardEnum.system_information -> {
                            val response = KolumbusResponse(
                                data = parseKolumbusResponse(it.getFetchUrl(operator)!!)
                            ).toSystemInformation()
                            cache.setResponseInCacheAndGet(operator, GbfsStandardEnum.station_information, response)
                        }
                        GbfsStandardEnum.station_information -> {
                            val response = KolumbusResponse(
                                data = parseKolumbusResponse(
                                    it.getFetchUrl(operator)!!
                                )
                            )
                            cache.setResponseInCacheAndGet(operator, GbfsStandardEnum.station_status, response.toStationStatus().toNeTEx(operator))
                            cache.setResponseInCacheAndGet(operator, GbfsStandardEnum.station_information, response.toStationInformation().toNeTEx(operator))
                        }
                        GbfsStandardEnum.station_status -> { }
                    }
                }
            }
        }
        delay(POLL_INTERVAL)
    }
}
