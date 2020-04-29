package org.entur

import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty

fun main() {
    val server = embeddedServer(Jetty, watchPaths = listOf("bikeservice"), port = 8080, module = Application::module)
    server.start(wait = true)
}

fun Application.module() {
    routing {
        get("/") {
            call.respondText("Hello and welcome to Entur Bikeservice!", ContentType.Application.Json)
        }
        get("{operator}/gbfs.json") {
            val operator = getOperatorFromPathParam(call.parameters["operator"])
            call.respondText(Gson().toJson(parseResponse<BikeResponse>(operator.gbfs)), ContentType.Application.Json)
        }
        get("{operator}/system_information.json") {
            val operator = getOperatorFromPathParam(call.parameters["operator"])
            call.respondText(Gson().toJson(parseResponse<SystemInformationResponse>(operator.system_information)), ContentType.Application.Json)
        }
        get("{operator}/station_information.json") {
            val operator = getOperatorFromPathParam(call.parameters["operator"])
            call.respondText(Gson().toJson(parseResponse<StationInformationResponse>(operator.station_information)), ContentType.Application.Json)
        }
        get("{operator}/station_status.json") {
            val operator = getOperatorFromPathParam(call.parameters["operator"])
            call.respondText(Gson().toJson(parseResponse<StationStatusResponse>(operator.station_status)), ContentType.Application.Json)
        }
    }
}

suspend inline fun <reified T> parseResponse(url: String): T {
    with(HttpClient()) {
        val response = get<String>(url) { header("Client-Identifier", "entur-bikeservice") }
        return Gson().fromJson(response, T::class.java)
    }
}
