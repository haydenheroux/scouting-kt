package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.event.dereference
import io.github.haydenheroux.scouting.models.event.eventQuery
import io.github.haydenheroux.scouting.models.match.dereference
import io.github.haydenheroux.scouting.models.match.matchQuery
import io.github.haydenheroux.scouting.models.match.metricQuery
import io.github.haydenheroux.scouting.models.team.dereference
import io.github.haydenheroux.scouting.models.team.robotQuery
import io.github.haydenheroux.scouting.models.team.seasonQuery
import io.github.haydenheroux.scouting.models.team.teamQuery
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
            val team = db.getTeam(call.request.queryParameters.teamQuery())

            call.respond(team.dereference())
        }

        get("/get-season") {
            val season = db.getSeason(call.request.queryParameters.seasonQuery())

            call.respond(season.dereference())
        }

        get("/get-robot") {
            val robot = db.getRobot(call.request.queryParameters.robotQuery())

            call.respond(robot.dereference())
        }

        get("/get-events") {
            val events = db.getEvents()

            call.respond(events.map { it.dereference() })
        }

        get("/get-event") {
            val event = db.getEvent(call.request.queryParameters.eventQuery())

            call.respond(event.dereference())
        }

        get("/get-match") {
            val match = db.getMatch(call.request.queryParameters.matchQuery())

            call.respond(match.dereference())
        }

        get("/get-metric") {
            val metric = db.getMetric(call.request.queryParameters.metricQuery())

            call.respond(metric.dereference())
        }
    }
}
