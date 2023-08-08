package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.event.dereference
import io.github.haydenheroux.scouting.models.event.eventQuery
import io.github.haydenheroux.scouting.models.match.*
import io.github.haydenheroux.scouting.models.team.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
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

        post("/new-team") {
            val team = call.receive<Team>()

            db.insertTeam(team)

            call.respond(HttpStatusCode.OK)
        }

        post("/new-season") {
            val season = call.receive<Season>()

            val team = call.request.queryParameters.teamQuery()

            db.insertSeason(season, team)

            call.respond(HttpStatusCode.OK)
        }

        post("/new-robot") {
            val robot = call.receive<Robot>()

            val season = call.request.queryParameters.seasonQuery()

            db.insertRobot(robot, season)

            call.respond(HttpStatusCode.OK)
        }

        post("/add-event") {
            val event = call.request.queryParameters.eventQuery()
            val season = call.request.queryParameters.seasonQuery()

            db.insertSeasonEvent(event, season)

            call.respond(HttpStatusCode.OK)
        }

        post("/new-event") {
            val event = call.receive<Event>()

            db.insertEvent(event)

            call.respond(HttpStatusCode.OK)
        }

        post("/new-match") {
            val match = call.receive<Match>()

            assert(match.metrics.isEmpty())

            val event = call.request.queryParameters.eventQuery()

            db.insertMatch(match, event)

            call.respond(HttpStatusCode.OK)
        }

        post("/new-metric") {
            val metric = call.receive<Metric>()

            val match = call.request.queryParameters.matchQuery()
            val robot = call.request.queryParameters.robotQuery()

            db.insertMetric(metric, match, robot)

            call.respond(HttpStatusCode.OK)
        }
    }
}
