package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.match.GameMetric
import io.github.haydenheroux.scouting.models.match.Match
import io.github.haydenheroux.scouting.models.match.Metric
import io.github.haydenheroux.scouting.models.team.Robot
import io.github.haydenheroux.scouting.models.team.Season
import io.github.haydenheroux.scouting.models.team.Team

class DatabaseImplementation : DatabaseInterface {
    override fun fetchTeamById(teamId: Int): Team {
        TODO("Not yet implemented")
    }

    override fun fetchSeasonById(seasonId: Int): Season {
        TODO("Not yet implemented")
    }

    override fun fetchRobotById(robotId: Int): Robot {
        TODO("Not yet implemented")
    }

    override fun fetchSeasonsByTeamId(teamId: Int): List<Season> {
        TODO("Not yet implemented")
    }

    override fun fetchRobotsBySeasonId(seasonId: Int): List<Robot> {
        TODO("Not yet implemented")
    }

    override fun fetchEventsBySeasonId(seasonId: Int): List<Event> {
        TODO("Not yet implemented")
    }

    override fun fetchMatchesByEventId(eventId: Int): List<Match> {
        TODO("Not yet implemented")
    }

    override fun fetchMetricsByMatchId(matchId: Int): List<Metric> {
        TODO("Not yet implemented")
    }

    override fun fetchGameMetricsByMetricId(metricId: Int): List<GameMetric> {
        TODO("Not yet implemented")
    }
}

val db = DatabaseImplementation()
