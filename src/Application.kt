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

fun main(args: Array<String>) {
    val server = embeddedServer(Jetty, watchPaths = listOf("bikeservice"), port = 8080, module = Application::module)
    server.start(wait = true)
}

fun Application.module() {
    routing {
        get("/") {
            call.respondText("Hello and welcome to Entur Bikeservice!", ContentType.Application.Json)
        }
        get("/gbfs.json") {
            call.respondText(Gson().toJson(parseResponse<BikeResponse>(OsloBysykkelURL.gbfs)), ContentType.Application.Json)
        }
        get("/system_information.json") {
            call.respondText(Gson().toJson(parseResponse<SystemInformationResponse>(OsloBysykkelURL.system_information)), ContentType.Application.Json)
        }
        get("/station_information.json") {
            call.respondText(Gson().toJson(parseResponse<StationInformationResponse>(OsloBysykkelURL.station_information)), ContentType.Application.Json)
        }
        get("/station_status.json") {
            call.respondText(Gson().toJson(parseResponse<StationStatusResponse>(OsloBysykkelURL.station_status)), ContentType.Application.Json)
        }
    }
}

suspend inline fun <reified T> parseResponse(url: String): T {
    with(HttpClient()) {
        val response = get<String>(url) { header("Client-Identifier", "entur-bikeservice") }
        return Gson().fromJson(response, T::class.java)
    }
}
