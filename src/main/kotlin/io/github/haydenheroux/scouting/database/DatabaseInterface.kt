package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.event.EventNode
import io.github.haydenheroux.scouting.models.event.EventQuery
import io.github.haydenheroux.scouting.models.match.*
import io.github.haydenheroux.scouting.models.team.*

interface DatabaseInterface {
    suspend fun getTeams(): List<TeamNode>

    suspend fun getTeamByQuery(teamQuery: TeamQuery): TeamNode

    suspend fun getTeamBySeason(seasonData: SeasonNode): TeamNode

    suspend fun getTeamById(teamId: Int): TeamNode

    suspend fun getSeasonByQuery(seasonQuery: SeasonQuery): SeasonNode

    suspend fun getSeasonsByTeam(teamData: TeamNode): List<SeasonNode>

    suspend fun getSeasonByRobot(robotData: RobotNode): SeasonNode

    suspend fun getSeasonById(seasonId: Int): SeasonNode

    suspend fun getRobotByQuery(robotQuery: RobotQuery): RobotNode

    suspend fun getRobotsBySeason(seasonData: SeasonNode): List<RobotNode>

    suspend fun getRobotById(robotId: Int): RobotNode

    suspend fun getEvents(): List<EventNode>

    suspend fun getEventsBySeason(seasonData: SeasonNode): List<EventNode>

    suspend fun getEventByQuery(eventQuery: EventQuery): EventNode

    suspend fun getEventByMatch(matchData: MatchNode): EventNode

    suspend fun getEventById(eventId: Int): EventNode

    suspend fun getMatchesByEvent(eventData: EventNode): List<MatchNode>

    suspend fun getMatchByQuery(matchQuery: MatchQuery): MatchNode

    suspend fun getMatchByParticipant(participantData: ParticipantNode): MatchNode

    suspend fun getMatchById(matchId: Int): MatchNode

    suspend fun getParticipantsByMatch(matchData: MatchNode): List<ParticipantNode>

    suspend fun getParticipantByQuery(participantQuery: ParticipantQuery): ParticipantNode

    suspend fun getParticipantByMetric(metricData: MetricNode): ParticipantNode

    suspend fun getParticipantById(participantId: Int): ParticipantNode

    suspend fun getMetricsByParticipant(participantData: ParticipantNode): List<MetricNode>

    suspend fun insertTeam(team: Team)
    suspend fun insertSeason(season: Season, teamQuery: TeamQuery)
    suspend fun insertSeasonEvent(eventQuery: EventQuery, seasonQuery: SeasonQuery)
    suspend fun insertRobot(robot: Robot, seasonQuery: SeasonQuery)
    suspend fun insertEvent(event: Event)
    suspend fun insertMatch(match: Match, eventQuery: EventQuery)
    suspend fun insertParticipant(participant: Participant, matchQuery: MatchQuery)
}
