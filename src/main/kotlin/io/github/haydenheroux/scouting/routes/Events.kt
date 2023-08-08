package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.events() {
    route("/events") {
        get {
            val eventReferences = db.getEvents()

            call.respond(FreeMarkerContent("events/events.ftl", mapOf("eventReferences" to eventReferences)))
        }
    }
}
