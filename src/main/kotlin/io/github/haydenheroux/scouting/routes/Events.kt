package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.query.TeamQuery
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.events() {
    route("/events") {
        get {
            val events = db.getEvents()

            // TODO
            val robot = db.getTeam(TeamQuery(5112)).seasons[0].robots[0]
            val season = db.getTeam(TeamQuery(5112)).seasons[0]
            val team = db.getTeam(TeamQuery(5112))
            robot.season = season
            season.team = team

            for (event in events) {
                for (match in event.matches) {
                    for (metric in match.metrics) {
                        metric.robot = robot
                    }
                }
            }

            call.respond(FreeMarkerContent("events.ftl", mapOf("events" to events)))
        }
    }
}
