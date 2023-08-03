package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.match.GameMetric
import io.github.haydenheroux.scouting.models.match.Match
import io.github.haydenheroux.scouting.models.match.Metric
import io.github.haydenheroux.scouting.models.team.Robot
import io.github.haydenheroux.scouting.models.team.Season
import io.github.haydenheroux.scouting.models.team.Team
import io.github.haydenheroux.scouting.query.*

interface DatabaseInterface {
    /**
     * Gets all teams.
     *
     * @return the list containing all teams.
     */
    suspend fun getTeams(): List<Team>

    suspend fun getTeam(teamQuery: TeamQuery): Team

    suspend fun getSeason(seasonQuery: SeasonQuery): Season

    suspend fun getRobot(robotQuery: RobotQuery): Robot

    /**
     * Gets all events.
     *
     * @return the list containing all teams.
     */
    suspend fun getEvents(): List<Event>

    suspend fun getEvent(eventQuery: EventQuery): Event

    suspend fun getMatch(matchQuery: MatchQuery): Match

    suspend fun getMetric(metricQuery: MetricQuery): Metric

    suspend fun insertTeam(team: Team)
    suspend fun insertSeason(season: Season)
    suspend fun insertRobot(robot: Robot)
    suspend fun insertEvent(event: Event)
    suspend fun insertSeasonEvent(event: Event, season: Season)
    suspend fun insertMatch(match: Match)
    suspend fun insertMetric(metric: Metric)
    suspend fun insertGameMetric(gameMetric: GameMetric)
}
