package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.database.sql.tables.*
import io.github.haydenheroux.scouting.models.*

interface DatabaseInterface {
    suspend fun getTeams(): Result<List<TeamNode>>

    suspend fun getTeamByQuery(teamQuery: TeamQuery): Result<TeamNode>

    suspend fun getTeamBySeason(seasonData: SeasonNode): Result<TeamNode>

    suspend fun getTeamByParticipant(participantData: ParticipantNode): Result<TeamNode>

    suspend fun getTeamById(teamId: Int): Result<TeamNode>

    suspend fun getSeasonByQuery(seasonQuery: SeasonQuery): Result<SeasonNode>

    suspend fun getSeasonsByTeam(teamData: TeamNode): Result<List<SeasonNode>>

    suspend fun getSeasonByRobot(robotData: RobotNode): Result<SeasonNode>

    suspend fun getSeasonById(seasonId: Int): Result<SeasonNode>

    suspend fun getRobotByQuery(robotQuery: RobotQuery): Result<RobotNode>

    suspend fun getRobotsBySeason(seasonData: SeasonNode): Result<List<RobotNode>>

    suspend fun getRobotById(robotId: Int): Result<RobotNode>

    suspend fun getEvents(): Result<List<EventNode>>

    suspend fun getEventsBySeason(seasonData: SeasonNode): Result<List<EventNode>>

    suspend fun getEventByQuery(eventQuery: EventQuery): Result<EventNode>

    suspend fun getEventByMatch(matchData: MatchNode): Result<EventNode>

    suspend fun getEventById(eventId: Int): Result<EventNode>

    suspend fun getMatchesByEvent(eventData: EventNode): Result<List<MatchNode>>

    suspend fun getMatchByQuery(matchQuery: MatchQuery): Result<MatchNode>

    suspend fun getMatchByParticipant(participantData: ParticipantNode): Result<MatchNode>

    suspend fun getMatchById(matchId: Int): Result<MatchNode>

    suspend fun getParticipantsByMatch(matchData: MatchNode): Result<List<ParticipantNode>>

    suspend fun getParticipantByQuery(participantQuery: ParticipantQuery): Result<ParticipantNode>

    suspend fun getParticipantByMetric(metricData: MetricNode): Result<ParticipantNode>

    suspend fun getParticipantById(participantId: Int): Result<ParticipantNode>

    suspend fun getMetricsByParticipant(participantData: ParticipantNode): Result<List<MetricNode>>

    suspend fun insertTeam(team: Team): Result<Unit>
    suspend fun insertSeason(season: Season, teamQuery: TeamQuery): Result<Unit>
    suspend fun insertSeasonEvent(eventQuery: EventQuery, seasonQuery: SeasonQuery): Result<Unit>
    suspend fun insertRobot(robot: Robot, seasonQuery: SeasonQuery): Result<Unit>
    suspend fun insertEvent(event: Event): Result<Unit>
    suspend fun insertMatch(match: Match, eventQuery: EventQuery): Result<Unit>
    suspend fun insertParticipant(participant: Participant, teamQuery: TeamQuery, matchQuery: MatchQuery): Result<Unit>
}
