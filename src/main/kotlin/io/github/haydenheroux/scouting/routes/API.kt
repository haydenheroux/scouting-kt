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

            for (season in team.seasons) {
                season.team = team

                for (robot in season.robots) {
                    robot.season = season
                }

                for (event in season.events) {
                    for (match in event.matches) {
                        match.event = event

                        for (metric in match.metrics) {
                            metric.match = match
                            // TODO Find the correct robot for this match
                            metric.robot = season.robots[0]

                            for (gameMetric in metric.gameMetrics) {
                                gameMetric.metric = metric
                            }
                        }
                    }
                }
            }

            db.insertTeam(team)
        }
    }
}
