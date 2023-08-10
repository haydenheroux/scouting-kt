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
            val teams = db.getTeams().map { team -> team.tree().leaf() }

            call.respond(FreeMarkerContent("teams/teams.ftl", mapOf("teams" to teams)))
        }

        get("/{team}") {
            val teamQuery = call.parameters.teamQuery().getOrNull()

            if (teamQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val EVENTS_ONLY = 2
            val EVENTS_AND_MATCHES = 4
            val team = db.getTeamByQuery(teamQuery).branch().tree().subtree(EVENTS_ONLY)

            call.respond(FreeMarkerContent("teams/team.ftl", mapOf("team" to team)))
        }

        get("/{team}/{year}") {
            val seasonQuery = call.parameters.seasonQuery().getOrNull()

            if (seasonQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val node = db.getSeasonByQuery(seasonQuery)

            val team = node.parent().team.tree().leaf()

            val EVENTS_ONLY = 1
            val EVENTS_AND_MATCHES = 4
            val season = node.branch().tree().subtree(EVENTS_AND_MATCHES)

            call.respond(FreeMarkerContent("teams/season.ftl", mapOf("team" to team, "season" to season)))
        }
    }
}
