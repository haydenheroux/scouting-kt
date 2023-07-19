package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.models.enums.Region
import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.match.GameMetric
import io.github.haydenheroux.scouting.models.match.Match
import io.github.haydenheroux.scouting.models.match.Metric
import io.github.haydenheroux.scouting.models.team.Robot
import io.github.haydenheroux.scouting.models.team.Season
import io.github.haydenheroux.scouting.models.team.Team

interface DatabaseInterface {
    /**
     * Gets all teams stored in the database.
     *
     * @return the list representing all teams stored in the database.
     */
    suspend fun getTeams(): List<Team>

    /**
     * Gets the team with the specified team number.
     *
     * If the team with the specified team number does not exist, the behavior
     * is undefined.
     *
     * @param number the number of the team.
     * @return the team with the specified team number.
     * @see Team.number
     */
    suspend fun getTeamByNumber(number: Int): Team

    suspend fun getEvents(): List<Event>

    suspend fun getEventsByRegion(region: Region): List<Event>

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
