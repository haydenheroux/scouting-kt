package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.teams() {
    route("/teams") {
        get {
            val teams = db.getTeams()

            call.respond(teams)
        }

        get("/{number}") {
            val number = call.parameters["number"]!!.toInt()

            val team = db.getTeams().single { it.number == number }

            call.respond(team)
        }

        get("/{number}/{year}") {
            val number = call.parameters["number"]!!.toInt()
            val year = call.parameters["year"]!!.toInt()

            val team = db.getTeams().single { it.number == number }
            val season = team.seasons.single { it.year == year }

            call.respond(season)
        }
    }
}
