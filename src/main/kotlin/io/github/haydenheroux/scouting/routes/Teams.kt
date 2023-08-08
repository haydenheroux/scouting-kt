package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.team.seasonQuery
import io.github.haydenheroux.scouting.models.team.teamQuery
import io.ktor.http.*
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

        get("/{team}") {
            val teamQuery = call.parameters.teamQuery().getOrNull()

            teamQuery?.let {
                val teamReference = db.getTeam(teamQuery)

                call.respond(FreeMarkerContent("team.ftl", mapOf("teamReference" to teamReference)))
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/{team}/{year}") {
            val seasonQuery = call.parameters.seasonQuery().getOrNull()

            seasonQuery?.let {
                val seasonReference = db.getSeason(seasonQuery)

                call.respond(FreeMarkerContent("season.ftl", mapOf("seasonReference" to seasonReference)))
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}
