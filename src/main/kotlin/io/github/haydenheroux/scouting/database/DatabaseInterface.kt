package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.match.GameMetric
import io.github.haydenheroux.scouting.models.match.Match
import io.github.haydenheroux.scouting.models.match.Metric
import io.github.haydenheroux.scouting.models.team.Robot
import io.github.haydenheroux.scouting.models.team.Season
import io.github.haydenheroux.scouting.models.team.Team

interface DatabaseInterface {
    suspend fun fetchTeamById(teamId: Int): Team
    suspend fun fetchSeasonById(seasonId: Int): Season
    suspend fun fetchRobotById(robotId: Int): Robot

    suspend fun fetchSeasonsByTeamId(teamId: Int): List<Season>
    suspend fun fetchRobotsBySeasonId(seasonId: Int): List<Robot>
    suspend fun fetchEventsBySeasonId(seasonId: Int): List<Event>
    suspend fun fetchMatchesByEventId(eventId: Int): List<Match>
    suspend fun fetchMetricsByMatchId(matchId: Int): List<Metric>
    suspend fun fetchGameMetricsByMetricId(metricId: Int): List<GameMetric>
}
