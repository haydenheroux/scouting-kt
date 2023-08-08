package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.event.eventQuery
import io.ktor.http.*
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

        get("/{region}/{year}/{week}/{event}") {
            val eventQuery = call.parameters.eventQuery().getOrNull()

            eventQuery?.let {
                val eventReference = db.getEvent(eventQuery)

                call.respond(FreeMarkerContent("events/event.ftl", mapOf("eventReference" to eventReference)))
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}
