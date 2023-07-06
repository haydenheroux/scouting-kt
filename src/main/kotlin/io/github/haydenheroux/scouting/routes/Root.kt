package io.github.haydenheroux.scouting.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.root() {
    route("/") {
        get {
            call.respondText("Hello, world!")
        }
    }
}