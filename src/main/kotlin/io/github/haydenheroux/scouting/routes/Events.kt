package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.models.eventQueryOf
import io.github.haydenheroux.scouting.models.matchQueryOf
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.events() {
    route("/events") {
        get {
            SQLDatabase.getEventsSimple().getOrNull()?.let { events ->
                call.respond(FreeMarkerContent("events/events.ftl", mapOf("events" to events)))
            } ?: run {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        get("/{region}/{year}/{week}/{event}") {
            val eventQuery = eventQueryOf(call.parameters).getOrNull()

            if (eventQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            SQLDatabase.getEventWithTeamNumbers(eventQuery).getOrNull()?.let { event ->
                call.respond(FreeMarkerContent("events/event.ftl", mapOf("event" to event)))
            } ?: run {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/{region}/{year}/{week}/{event}/{match}") {
            val matchQuery = matchQueryOf(call.parameters).getOrNull()

            if (matchQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            SQLDatabase.getMatchWithMetricsAndEvent(matchQuery).getOrNull()?.let { matchAndEvent ->
                call.respond(
                    FreeMarkerContent(
                        "events/match.ftl",
                        mapOf("match" to matchAndEvent.first, "event" to matchAndEvent.second)
                    )
                )
            } ?: run {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
