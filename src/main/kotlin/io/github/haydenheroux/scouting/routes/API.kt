package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.team.Robot
import io.github.haydenheroux.scouting.models.team.Season
import io.github.haydenheroux.scouting.models.team.Team
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

            val number = call.request.queryParameters["team"]!!.toInt()
            val team = db.getTeamByNumber(number)

            season.team = team

            db.insertSeason(season)

            call.respond(HttpStatusCode.OK)
        }

        post("/new-robot") {
            val robot = call.receive<Robot>()

            val number = call.request.queryParameters["team"]!!.toInt()
            val team = db.getTeamByNumber(number)

            // TODO Refactor to avoid back-linking the team here, do entirely in DB instead?
            val year = call.request.queryParameters["season"]!!.toInt()
            val season = team.seasons.single { it.year == year }
            season.team = team

            robot.season = season

            db.insertRobot(robot)

            call.respond(HttpStatusCode.OK)
        }

        post("/new-event") {
            val event = call.receive<Event>()

            assert(event.matches.isEmpty())

            db.insertEvent(event)

            call.respond(HttpStatusCode.OK)
        }
    }
}
