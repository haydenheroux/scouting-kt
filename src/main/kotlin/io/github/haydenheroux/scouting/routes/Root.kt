package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.models.enums.Alliance
import io.github.haydenheroux.scouting.models.enums.MatchType
import io.github.haydenheroux.scouting.models.enums.Region
import io.github.haydenheroux.scouting.models.event.Events
import io.github.haydenheroux.scouting.models.event.SeasonEvents
import io.github.haydenheroux.scouting.models.match.GameMetrics
import io.github.haydenheroux.scouting.models.match.Matches
import io.github.haydenheroux.scouting.models.match.Metrics
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
            it[region] = Region.NEW_ENGLAND
        }

        // Insert seasons
        val seasonId = Seasons.insertAndGetId {
            it[team] = teamId
            it[year] = 2021
        }

        // Insert robots
        val robotId = Robots.insertAndGetId {
            it[season] = seasonId
            it[name] = "Robot A1"
        }

        // Insert events
        val eventId = Events.insertAndGetId {
            it[name] = "Event A"
            it[region] = Region.NEW_ENGLAND
            it[year] = 2021
            it[week] = 1
        }

        SeasonEvents.insert {
            it[season] = seasonId
            it[event] = eventId
        }

        // Insert matches
        val matchId = Matches.insertAndGetId {
            it[number] = 1
            it[type] = MatchType.QUALIFICATION
            it[event] = eventId
        }

        // Insert metrics
        val metricId = Metrics.insertAndGetId {
            it[match] = matchId
            it[robot] = robotId
            it[alliance] = Alliance.RED
        }

        // Insert game metrics
        GameMetrics.insert {
            it[metric] = metricId
            it[key] = "Score"
            it[value] = "100"
        }
    }
}
