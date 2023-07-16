package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.team.Season
import io.github.haydenheroux.scouting.models.team.Team
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
            val number: Int = call.parameters["number"]!!.toInt()

            val team: Team = db.getTeamByNumber(number)

            call.respond(team)
        }

        get("/{number}/{year}") {
            val number: Int = call.parameters["number"]!!.toInt()
            val year: Int = call.parameters["year"]!!.toInt()

            val team = db.getTeamByNumber(number)
            val season: Season = team.seasons.filter { it.year == year }[0]

            call.respond(season)
        }
    }
}
