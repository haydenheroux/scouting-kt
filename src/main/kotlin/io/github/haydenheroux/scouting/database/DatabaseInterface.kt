package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.event.EventReference
import io.github.haydenheroux.scouting.models.match.*
import io.github.haydenheroux.scouting.models.team.*
import io.github.haydenheroux.scouting.query.*

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
    suspend fun insertSeason(season: Season)
    suspend fun insertRobot(robot: Robot)
    suspend fun insertEvent(event: Event)
    suspend fun insertSeasonEvent(event: Event, season: Season)
    suspend fun insertMatch(match: Match)
    suspend fun insertMetric(metric: Metric)
    suspend fun insertGameMetric(gameMetric: GameMetric)
}
