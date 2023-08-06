package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.teams() {
    route("/teams") {
        get {
            val teams = db.getTeams()

            call.respond(FreeMarkerContent("teams.ftl", mapOf("teams" to teams)))
        }

        get("/{number}") {
            val number = call.parameters["number"]!!.toInt()

            val team = db.getTeams().single { it.number == number }

            call.respond(FreeMarkerContent("team.ftl", mapOf("team" to team)))
        }

        get("/{number}/{year}") {
            val number = call.parameters["number"]!!.toInt()
            val year = call.parameters["year"]!!.toInt()

            val team = db.getTeams().single { it.number == number }
            val season = team.seasons.single { it.year == year }

            // TODO testme
            call.respond(FreeMarkerContent("season.ftl", mapOf("season" to season)))
        }
    }
}
