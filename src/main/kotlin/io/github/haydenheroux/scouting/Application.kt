package io.github.haydenheroux.scouting

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.plugins.configureIgnoreTrailingSlash
import io.github.haydenheroux.scouting.plugins.configureRouting
import io.github.haydenheroux.scouting.plugins.configureSerialization
import io.github.haydenheroux.scouting.plugins.configureTemplating
import io.ktor.server.application.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    SQLDatabase.init()
    configureRouting()
    configureSerialization()
    configureTemplating()
    configureIgnoreTrailingSlash()
}
