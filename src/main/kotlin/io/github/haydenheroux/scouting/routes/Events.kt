package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.sql.db
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
            db.getEvents().getOrNull()?.let { nodes ->
                val events = nodes.map { node -> node.tree().leaf() }

                call.respond(FreeMarkerContent("events/events.ftl", mapOf("events" to events)))
            } ?: run {
                call.respond(HttpStatusCode.InternalServerError)
            }
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

            db.getEventByQuery(eventQuery).getOrNull()?.let { node ->
                val event = node.branch().tree().subtree(MATCHES_AND_TEAMS)

                call.respond(FreeMarkerContent("events/event.ftl", mapOf("event" to event)))
            } ?: run {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/{region}/{year}/{week}/{event}/{match}") {
            val matchQuery = call.parameters.matchQuery().getOrNull()

            if (matchQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            // TODO = 3 bug, long wait time, cycle?
            val TEAM_AND_METRICS = 2

            db.getMatchByQuery(matchQuery).getOrNull()?.let { node ->
                val event = node.parent().event.branch().tree().leaf()
                val match = node.branch().tree().subtree(TEAM_AND_METRICS)

                call.respond(FreeMarkerContent("events/match.ftl", mapOf("event" to event, "match" to match)))
            } ?: run {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
