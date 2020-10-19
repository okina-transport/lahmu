package org.entur.lahmu

import io.ktor.server.engine.EngineAPI
import io.ktor.util.KtorExperimentalAPI
import org.entur.lahmu.config.setup
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("org.entur.lahmu")

@KtorExperimentalAPI
@EngineAPI
fun main() {
    setup().start(wait = true)
}
