package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.errors.Error
import io.github.haydenheroux.scouting.errors.Success
import io.github.haydenheroux.scouting.errors.getHttpStatusCode
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
            val teamsOrError = SQLDatabase.getTeamsSimple()

            if (teamsOrError is Error) {
                call.respond(teamsOrError.error.getHttpStatusCode())
                return@get
            }

            val teams = (teamsOrError as Success).value
            call.respond(FreeMarkerContent("teams/teams.ftl", mapOf("teams" to teams)))
        }

        get("/{team}") {
            val teamQuery = teamQueryOf(call.parameters).getOrNull()

            if (teamQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val teamOrError = SQLDatabase.getTeamWithEvents(teamQuery)

            if (teamOrError is Error) {
                call.respond(teamOrError.error.getHttpStatusCode())
                return@get
            }

            val team = (teamOrError as Success).value

            call.respond(FreeMarkerContent("teams/team.ftl", mapOf("team" to team)))
        }

        get("/{team}/{year}") {
            val seasonQuery = seasonQueryOf(call.parameters).getOrNull()

            if (seasonQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val seasonAndTeamOrError = SQLDatabase.getSeasonWithMatchesAndTeam(seasonQuery)

            if (seasonAndTeamOrError is Error) {
                call.respond(seasonAndTeamOrError.error.getHttpStatusCode())
                return@get
            }

            val seasonAndTeam = (seasonAndTeamOrError as Success).value

            call.respond(
                FreeMarkerContent(
                    "teams/season.ftl",
                    mapOf("season" to seasonAndTeam.first, "team" to seasonAndTeam.second)
                )
            )
        }
    }
}
