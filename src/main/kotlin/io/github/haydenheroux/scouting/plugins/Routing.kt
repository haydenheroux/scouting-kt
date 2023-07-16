package io.github.haydenheroux.scouting.plugins

import io.github.haydenheroux.scouting.routes.root
import io.github.haydenheroux.scouting.routes.teams
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        root()
        teams()
    }
}
