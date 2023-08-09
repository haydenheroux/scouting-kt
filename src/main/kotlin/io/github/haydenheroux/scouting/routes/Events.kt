package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.event.eventQuery
import io.github.haydenheroux.scouting.models.match.matchQuery
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.events() {
    route("/events") {
        get {
            val events = db.getEvents()

            call.respond(FreeMarkerContent("events/events.ftl", mapOf("events" to events)))
        }

        get("/{region}/{year}/{week}/{event}") {
            val eventQuery = call.parameters.eventQuery().getOrNull()

            eventQuery?.let {
                val event = db.getEventByQuery(eventQuery)

                call.respond(FreeMarkerContent("events/event.ftl", mapOf("event" to event)))
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/{region}/{year}/{week}/{event}/{match}") {
            val matchQuery = call.parameters.matchQuery().getOrNull()

            matchQuery?.let {
                val match = db.getMatchByQuery(matchQuery)

                call.respond(FreeMarkerContent("events/match.ftl", mapOf("match" to match)))
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}
