package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.query.seasonQueryFromParameters
import io.github.haydenheroux.scouting.query.teamQueryFromParameters
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
            val team = db.getTeam(teamQueryFromParameters(call.parameters))

            call.respond(FreeMarkerContent("team.ftl", mapOf("team" to team)))
        }

        get("/{team}/{year}") {
            val season = db.getSeason(seasonQueryFromParameters(call.parameters))

            call.respond(FreeMarkerContent("season.ftl", mapOf("season" to season)))
        }
    }
}
