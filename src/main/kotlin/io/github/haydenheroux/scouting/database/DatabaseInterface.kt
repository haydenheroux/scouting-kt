package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.event.EventQuery
import io.github.haydenheroux.scouting.models.event.EventReference
import io.github.haydenheroux.scouting.models.match.*
import io.github.haydenheroux.scouting.models.team.*

interface DatabaseInterface {
    suspend fun getTeams(): List<TeamReference>

    suspend fun getTeam(teamQuery: TeamQuery): TeamReference

    suspend fun getSeason(seasonQuery: SeasonQuery): SeasonReference

    suspend fun getRobot(robotQuery: RobotQuery): RobotReference

    suspend fun getEvents(): List<EventReference>

    suspend fun getEvent(eventQuery: EventQuery): EventReference

    suspend fun getMatch(matchQuery: MatchQuery): MatchReference

    suspend fun getMetric(metricQuery: MetricQuery): MetricReference

    suspend fun insertTeam(team: Team)
    suspend fun insertSeason(season: Season, teamQuery: TeamQuery)
    suspend fun insertSeasonEvent(eventQuery: EventQuery, seasonQuery: SeasonQuery)
    suspend fun insertRobot(robot: Robot, seasonQuery: SeasonQuery)
    suspend fun insertEvent(event: Event)
    suspend fun insertMatch(match: Match, eventQuery: EventQuery)
    suspend fun insertMetric(metric: Metric, matchQuery: MatchQuery, robotQuery: RobotQuery)
}
