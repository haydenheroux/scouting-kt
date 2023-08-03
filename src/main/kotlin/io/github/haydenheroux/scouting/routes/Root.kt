package io.github.haydenheroux.scouting.routes

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.root() {
    staticResources("/static", "files")

    route("/") {
        get {
            call.respondText("WIP")
        }
    }
}
