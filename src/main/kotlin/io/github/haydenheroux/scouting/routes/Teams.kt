package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.models.seasonQueryOf
import io.github.haydenheroux.scouting.models.teamQueryOf
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.teams() {
    route("/teams") {
        get {
            SQLDatabase.getTeamsSimple().getOrNull()?.let { teams ->
                call.respond(FreeMarkerContent("teams/teams.ftl", mapOf("teams" to teams)))
            } ?: run {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        get("/{team}") {
            val teamQuery = teamQueryOf(call.parameters).getOrNull()

            if (teamQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            SQLDatabase.getTeamWithEvents(teamQuery).getOrNull()?.let { team ->
                call.respond(FreeMarkerContent("teams/team.ftl", mapOf("team" to team)))
            } ?: run {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/{team}/{year}") {
            val seasonQuery = seasonQueryOf(call.parameters).getOrNull()

            if (seasonQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val EVENTS_ONLY = 1
            val EVENTS_AND_MATCHES = 4

            SQLDatabase.getSeasonByQuery(seasonQuery).getOrNull()?.let { node ->
                val team = node.parent().team.tree().leaf()
                val season = node.branch().tree().subtree(EVENTS_AND_MATCHES)

                call.respond(FreeMarkerContent("teams/season.ftl", mapOf("team" to team, "season" to season)))
            } ?: run {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
