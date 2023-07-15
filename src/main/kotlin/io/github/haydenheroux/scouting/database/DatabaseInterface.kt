package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.match.GameMetric
import io.github.haydenheroux.scouting.models.match.Match
import io.github.haydenheroux.scouting.models.match.Metric
import io.github.haydenheroux.scouting.models.team.Robot
import io.github.haydenheroux.scouting.models.team.Season
import io.github.haydenheroux.scouting.models.team.Team

interface DatabaseInterface {
    fun fetchTeamById(teamId: Int): Team
    fun fetchSeasonById(seasonId: Int): Season
    fun fetchRobotById(robotId: Int): Robot

    fun fetchSeasonsByTeamId(teamId: Int): List<Season>
    fun fetchRobotsBySeasonId(seasonId: Int): List<Robot>
    fun fetchEventsBySeasonId(seasonId: Int): List<Event>
    fun fetchMatchesByEventId(eventId: Int): List<Match>
    fun fetchMetricsByMatchId(matchId: Int): List<Metric>
    fun fetchGameMetricsByMetricId(metricId: Int): List<GameMetric>
}
