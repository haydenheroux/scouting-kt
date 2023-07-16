package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.match.GameMetric
import io.github.haydenheroux.scouting.models.match.Match
import io.github.haydenheroux.scouting.models.match.Metric
import io.github.haydenheroux.scouting.models.team.Robot
import io.github.haydenheroux.scouting.models.team.Season
import io.github.haydenheroux.scouting.models.team.Team

interface DatabaseInterface {
    suspend fun getTeams(): List<Team>
    suspend fun getTeamByNumber(number: Int): Team

    suspend fun findTeam(teamId: Int): Team
    suspend fun findSeason(seasonId: Int): Season
    suspend fun findRobot(robotId: Int): Robot

    suspend fun findSeasons(teamId: Int): List<Season>
    suspend fun findRobots(seasonId: Int): List<Robot>
    suspend fun findEvents(seasonId: Int): List<Event>
    suspend fun findMatches(eventId: Int): List<Match>
    suspend fun findMetrics(matchId: Int): List<Metric>
    suspend fun findGameMetrics(metricId: Int): List<GameMetric>
}
