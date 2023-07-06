package io.github.haydenheroux.scouting

import io.github.haydenheroux.scouting.plugins.configureRouting
import io.github.haydenheroux.scouting.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureRouting()
    configureSerialization()
}