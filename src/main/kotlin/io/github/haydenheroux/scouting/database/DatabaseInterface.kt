package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.models.event.EventReference
import io.github.haydenheroux.scouting.models.match.MatchReference
import io.github.haydenheroux.scouting.models.match.MetricReference
import io.github.haydenheroux.scouting.models.team.RobotReference
import io.github.haydenheroux.scouting.models.team.SeasonReference
import io.github.haydenheroux.scouting.models.team.TeamReference
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
}
