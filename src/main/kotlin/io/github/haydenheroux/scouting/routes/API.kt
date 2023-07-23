package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.team.Team
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Route.api() {
    route("/api") {
        post("/team") {
            val team = call.receive<Team>()
            db.insertTeam(team)
        }
    }
}
