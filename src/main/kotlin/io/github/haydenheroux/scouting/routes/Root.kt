package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.models.event.Events
import io.github.haydenheroux.scouting.models.event.SeasonEvents
import io.github.haydenheroux.scouting.models.match.*
import io.github.haydenheroux.scouting.models.team.Robots
import io.github.haydenheroux.scouting.models.team.Seasons
import io.github.haydenheroux.scouting.models.team.Teams
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
            call.respondText("Inserted test data.")
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
        val eventId = Events.insertAndGetId {
            it[name] = "Event A"
            it[location] = "Location X"
            it[year] = 2021
            it[week] = 1
        }

        SeasonEvents.insert {
            it[season] = seasonId
            it[event] = eventId
        }
    }
}
