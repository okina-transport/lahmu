package org.entur

import com.google.gson.Gson
import io.ktor.application.*
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
                call.respondText(Gson().toJson(bysykkelRequest().data), ContentType.Application.Json)
            }
        }
    }

    server.start(wait = true)
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
}

suspend fun bysykkelRequest(): BikeResponse {
    with(HttpClient()) {
        val response = get<String>(osloBysykkelIndexUrl) {header("Client-Identifier", "entur-bikeservice")}
        return Gson().fromJson(response, BikeResponse::class.java)
    }
}