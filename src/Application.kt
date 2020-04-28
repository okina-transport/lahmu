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
    val server = embeddedServer(Jetty, port = 8080) {
        routing {
            get("/") {
                call.respondText(Gson().toJson(parseResponse<BikeResponse>(osloBysykkelIndexUrl).data), ContentType.Application.Json)
            }
            get("/gbfs.json") {
                call.respondText(Gson().toJson(parseResponse<BikeResponse>(osloBysykkelIndexUrl)), ContentType.Application.Json)
            }
            get("/system_information.json") {
                call.respondText(Gson().toJson(parseResponse<SystemInformationResponse>("http://gbfs.urbansharing.com/oslobysykkel.no/system_information.json")), ContentType.Application.Json)
            }
            get("/station_information.json") {
                call.respondText(Gson().toJson(parseResponse<StationInformationResponse>("http://gbfs.urbansharing.com/oslobysykkel.no/station_information.json")), ContentType.Application.Json)
            }
            get("/station_status.json") {
                call.respondText(Gson().toJson(parseResponse<StationStatusResponse>("http://gbfs.urbansharing.com/oslobysykkel.no/station_status.json")), ContentType.Application.Json)
            }
        }
    }

    server.start(wait = true)
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
}

suspend inline fun <reified T> parseResponse(url: String): T {
    with(HttpClient()) {
        val response = get<String>(url) { header("Client-Identifier", "entur-bikeservice") }
        return Gson().fromJson(response, T::class.java)
    }
}
