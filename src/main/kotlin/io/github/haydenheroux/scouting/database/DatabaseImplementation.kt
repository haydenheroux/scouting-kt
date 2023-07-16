package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.event.SeasonEvents
import io.github.haydenheroux.scouting.models.event.toEvent
import io.github.haydenheroux.scouting.models.match.*
import io.github.haydenheroux.scouting.models.team.*
import org.jetbrains.exposed.sql.select

class DatabaseImplementation : DatabaseInterface {
    override suspend fun fetchTeamById(teamId: Int): Team {
        return query {
            Teams.select { Teams.id eq teamId }.map { it.toTeam() }[0]
        }
    }

    override suspend fun fetchSeasonById(seasonId: Int): Season {
        return query {
            Seasons.select { Seasons.id eq seasonId }.map { it.toSeason() }[0]
        }
    }

    override suspend fun fetchRobotById(robotId: Int): Robot {
        return query {
            Robots.select { Robots.id eq robotId }.map { it.toRobot() }[0]
        }
    }

    override suspend fun fetchSeasonsByTeamId(teamId: Int): List<Season> {
        return query {
            Seasons.select { Seasons.team eq teamId }.map { it.toSeason() }
        }
    }

    override suspend fun fetchRobotsBySeasonId(seasonId: Int): List<Robot> {
        return query {
            Robots.select { Robots.season eq seasonId }.map { it.toRobot() }
        }
    }

    override suspend fun fetchEventsBySeasonId(seasonId: Int): List<Event> {
        return query {
            SeasonEvents.select { SeasonEvents.season eq seasonId }.map { it.toEvent() }
        }
    }

    override suspend fun fetchMatchesByEventId(eventId: Int): List<Match> {
        return query {
            Matches.select { Matches.event eq eventId }.map { it.toMatch() }
        }
    }

    override suspend fun fetchMetricsByMatchId(matchId: Int): List<Metric> {
        TODO("FUCK #2")
    }

    override suspend fun fetchGameMetricsByMetricId(metricId: Int): List<GameMetric> {
        return query {
            GameMetrics.select { GameMetrics.metric eq metricId }.map { it.toGameMetric() }
        }
    }
}

val db = DatabaseImplementation()
