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
            val events = db.getEvents().map { event -> event.tree().leaf() }

            call.respond(FreeMarkerContent("events/events.ftl", mapOf("events" to events)))
        }

        get("/{region}/{year}/{week}/{event}") {
            val eventQuery = call.parameters.eventQuery().getOrNull()

            if (eventQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val MATCHES_ONLY = 1
            // TODO Store team number on Participant to avoid getting .metrics along with .team
            val MATCHES_AND_TEAMS = 3
            val event = db.getEventByQuery(eventQuery).branch().tree().subtree(MATCHES_AND_TEAMS)

            call.respond(FreeMarkerContent("events/event.ftl", mapOf("event" to event)))
        }

        get("/{region}/{year}/{week}/{event}/{match}") {
            val matchQuery = call.parameters.matchQuery().getOrNull()

            if (matchQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val node = db.getMatchByQuery(matchQuery)

            val event = node.parent().event.branch().tree().leaf()

            // TODO = 3 bug, long wait time, cycle?
            val TEAM_AND_METRICS = 2
            val match = node.branch().tree().subtree(TEAM_AND_METRICS)

            call.respond(FreeMarkerContent("events/match.ftl", mapOf("event" to event, "match" to match)))
        }
    }
}
