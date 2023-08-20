package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.errors.Error
import io.github.haydenheroux.scouting.errors.Success
import io.github.haydenheroux.scouting.errors.getHttpStatusCode
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
            val eventsOrError = SQLDatabase.getEventsSimple()

            if (eventsOrError is Error) {
                call.respond(eventsOrError.error.getHttpStatusCode())
                return@get
            }

            val events = (eventsOrError as Success).value

            call.respond(FreeMarkerContent("events/events.ftl", mapOf("events" to events)))
        }

        get("/{event}") {
            val eventQuery = eventQueryOf(call.parameters).getOrNull()

            if (eventQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val eventOrError = SQLDatabase.getEventWithTeamNumbers(eventQuery)

            if (eventOrError is Error) {
                call.respond(eventOrError.error.getHttpStatusCode())
                return@get
            }

            val event = (eventOrError as Success).value

            call.respond(FreeMarkerContent("events/event.ftl", mapOf("event" to event)))
        }

        get("/{event}/{match}") {
            val matchQuery = matchQueryOf(call.parameters).getOrNull()

            if (matchQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val matchAndEventOrError = SQLDatabase.getMatchWithMetricsAndEvent(matchQuery)

            if (matchAndEventOrError is Error) {
                call.respond(matchAndEventOrError.error.getHttpStatusCode())
                return@get
            }

            val matchAndEvent = (matchAndEventOrError as Success).value

            call.respond(
                FreeMarkerContent(
                    "events/match.ftl",
                    mapOf("match" to matchAndEvent.first, "event" to matchAndEvent.second)
                )
            )
        }
    }
}
