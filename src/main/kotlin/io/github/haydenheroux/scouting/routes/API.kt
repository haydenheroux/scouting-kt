package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.match.Match
import io.github.haydenheroux.scouting.models.match.Metric
import io.github.haydenheroux.scouting.models.team.Robot
import io.github.haydenheroux.scouting.models.team.Season
import io.github.haydenheroux.scouting.models.team.Team
import io.github.haydenheroux.scouting.models.team.dereference
import io.github.haydenheroux.scouting.query.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.api() {
    route("/api") {
        post("/new-team") {
            val team = call.receive<Team>()

            assert(team.seasons.isEmpty())

            db.insertTeam(team)

            call.respond(HttpStatusCode.OK)
        }

        post("/new-season") {
            val season = call.receive<Season>()

            assert(season.robots.isEmpty())
            assert(season.events.isEmpty())

            db.insertSeason(season)

            call.respond(HttpStatusCode.OK)
        }

        post("/new-robot") {
            val robot = call.receive<Robot>()

            db.insertRobot(robot)

            call.respond(HttpStatusCode.OK)
        }

        post("/add-event") {
            val event = db.getEvent(eventQueryFromParameters(call.request.queryParameters))

            val season = db.getSeason(seasonQueryFromParameters(call.request.queryParameters))

            // TODO
            // db.insertSeasonEvent(event, season)

            call.respond(HttpStatusCode.OK)
        }

        post("/new-event") {
            val event = call.receive<Event>()

            assert(event.matches.isEmpty())

            db.insertEvent(event)

            call.respond(HttpStatusCode.OK)
        }

        post("/new-match") {
            val match = call.receive<Match>()

            assert(match.metrics.isEmpty())

            db.insertMatch(match)

            call.respond(HttpStatusCode.OK)
        }

        post("/new-metric") {
            val metric = call.receive<Metric>()

            db.insertMetric(metric)

            call.respond(HttpStatusCode.OK)
        }

        get("/get-teams") {
            val teams = db.getTeams()

            call.respond(teams[0].dereference())
        }

        get("/get-team") {
            val team = db.getTeam(teamQueryFromParameters(call.request.queryParameters))

            call.respond(team)
        }

        get("/get-season") {
            val season = db.getSeason(seasonQueryFromParameters(call.request.queryParameters))

            call.respond(season)
        }

        get("/get-robot") {
            val robot = db.getRobot(robotQueryFromParameters(call.request.queryParameters))

            call.respond(robot)
        }

        get("/get-event") {
            val event = db.getEvent(eventQueryFromParameters(call.request.queryParameters))

            call.respond(event)
        }

        get("/get-match") {
            val match = db.getMatch(matchQueryFromParameters(call.request.queryParameters))

            call.respond(match)
        }

        get("/get-metric") {
            val metric = db.getMetric(metricQueryFromParameters(call.request.queryParameters))

            call.respond(metric)
        }
    }
}
