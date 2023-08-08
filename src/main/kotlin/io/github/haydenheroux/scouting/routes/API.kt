package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.event.dereference
import io.github.haydenheroux.scouting.models.match.dereference
import io.github.haydenheroux.scouting.models.team.dereference
import io.github.haydenheroux.scouting.query.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.api() {
    route("/api") {
        get("/get-teams") {
            val teams = db.getTeams()

            call.respond(teams.map { it.dereference() })
        }

        get("/get-team") {
            val team = db.getTeam(teamQueryFromParameters(call.request.queryParameters))

            call.respond(team.dereference())
        }

        get("/get-season") {
            val season = db.getSeason(seasonQueryFromParameters(call.request.queryParameters))

            call.respond(season.dereference())
        }

        get("/get-robot") {
            val robot = db.getRobot(robotQueryFromParameters(call.request.queryParameters))

            call.respond(robot.dereference())
        }

        get("/get-events") {
            val events = db.getEvents()

            call.respond(events.map { it.dereference() })
        }

        get("/get-event") {
            val event = db.getEvent(eventQueryFromParameters(call.request.queryParameters))

            call.respond(event.dereference())
        }

        get("/get-match") {
            val match = db.getMatch(matchQueryFromParameters(call.request.queryParameters))

            call.respond(match.dereference())
        }

        get("/get-metric") {
            val metric = db.getMetric(metricQueryFromParameters(call.request.queryParameters))

            call.respond(metric.dereference())
        }
    }
}
