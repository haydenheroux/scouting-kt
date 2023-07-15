package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.*
import io.github.haydenheroux.scouting.models.match.Alliance
import io.github.haydenheroux.scouting.models.match.MatchType
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.root() {
    route("/") {
        get {
            insertTestData()

            val teamId = 1
            val team = db.fetchTeamById(teamId)
            val seasons = db.fetchSeasonsByTeamId(teamId)

            // Display the retrieved data
            println("Team: ${team.name} (${team.number})")
            for (season in seasons) {
                println("Season ${season.year}:")
                for (robot in season.robots) {
                    println("  Robot: ${robot.name}")
                }
                for (event in season.events) {
                    println("  Event: ${event.name} (${event.location})")
                    for (match in event.matches) {
                        println("    Match ${match.number}:")
                        for (metric in match.metrics) {
                            println("      Metric: ${metric.alliance}")
                            for (gameMetric in metric.gameMetrics) {
                                println("        ${gameMetric.key}: ${gameMetric.value}")
                            }
                        }
                    }
                }
            }

            call.respondText("Done!")
        }
    }
}

fun insertTestData() {
    transaction {
        // Insert teams
        val teamId = Teams.insertAndGetId {
            it[number] = 1
            it[name] = "Team A"
            it[location] = "Location A"
        }

        // Insert seasons
        val seasonId = Seasons.insertAndGetId {
            it[team] = teamId
            it[year] = 2021
        }

        // Insert robots
        Robots.insert {
            it[season] = seasonId
            it[name] = "Robot A1"
        }

        // Insert metrics
        val metricId = Metrics.insertAndGetId {
            it[robot] = 1
            it[alliance] = Alliance.RED
        }

        // Insert game metrics
        GameMetrics.insert {
            it[metric] = metricId
            it[key] = "Score"
            it[value] = "100"
        }

        // Insert matches
        Matches.insert {
            it[number] = 1
            it[type] = MatchType.QUALIFICATION
            it[event] = 1
        }

        // Insert events
        Events.insert {
            it[name] = "Event A"
            it[location] = "Location X"
            it[year] = 2021
            it[week] = 1
        }
    }
}
