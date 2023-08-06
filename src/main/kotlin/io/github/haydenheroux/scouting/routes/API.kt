package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.match.Match
import io.github.haydenheroux.scouting.models.match.Metric
import io.github.haydenheroux.scouting.models.team.Robot
import io.github.haydenheroux.scouting.models.team.Season
import io.github.haydenheroux.scouting.models.team.Team
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

            season.team = teamQueryFromParameters(call.request.queryParameters)

            db.insertSeason(season)

            call.respond(HttpStatusCode.OK)
        }

        post("/new-robot") {
            val robot = call.receive<Robot>()

            robot.season = seasonQueryFromParameters(call.request.queryParameters)

            db.insertRobot(robot)

            call.respond(HttpStatusCode.OK)
        }

        post("/add-event") {
            val event = db.getEvent(eventQueryFromParameters(call.request.queryParameters))

            val season = db.getSeason(seasonQueryFromParameters(call.request.queryParameters))

            db.insertSeasonEvent(event, season)

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

            match.event = eventQueryFromParameters(call.request.queryParameters)

            db.insertMatch(match)

            call.respond(HttpStatusCode.OK)
        }

        post("/new-metric") {
            val metric = call.receive<Metric>()

            metric.match = matchQueryFromParameters(call.request.queryParameters)

            metric.robot = robotQueryFromParameters(call.request.queryParameters)

            for (gameMetric in metric.gameMetrics) {
                gameMetric.metric = metricQueryFromMetric(metric)
            }

            db.insertMetric(metric)

            call.respond(HttpStatusCode.OK)
        }
    }
}
