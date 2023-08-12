package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.database.sql.tables.*
import io.github.haydenheroux.scouting.models.*

interface DatabaseInterface {
    suspend fun getRobotByQuery(robotQuery: RobotQuery): Result<RobotNode>

    suspend fun getRobotsBySeason(seasonData: SeasonNode): Result<List<RobotNode>>

    suspend fun getRobotById(robotId: Int): Result<RobotNode>


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

    suspend fun teamExists(teamQuery: TeamQuery): Boolean

    suspend fun teamExists(team: Team): Boolean

    suspend fun getTeams(): Result<List<Team>>

    suspend fun getTeamsSimple(): Result<List<Team>>

    suspend fun getTeam(teamQuery: TeamQuery): Result<Team>

    suspend fun getTeamWithEvents(teamQuery: TeamQuery): Result<Team>

    suspend fun getTeamWithMatches(teamQuery: TeamQuery): Result<Team>


    suspend fun insertSeason(season: Season, teamQuery: TeamQuery): Result<Unit>

    suspend fun insertSeasonEvent(eventQuery: EventQuery, seasonQuery: SeasonQuery): Result<Unit>

    suspend fun seasonExists(seasonQuery: SeasonQuery): Boolean

    suspend fun seasonExists(season: Season, team: Team): Boolean

    suspend fun getSeason(seasonQuery: SeasonQuery): Result<Season>

    suspend fun getSeasonWithEventsAndTeam(seasonQuery: SeasonQuery): Result<Pair<Season, Team>>

    suspend fun getSeasonWithMatchesAndTeam(seasonQuery: SeasonQuery): Result<Pair<Season, Team>>


    suspend fun insertRobot(robot: Robot, seasonQuery: SeasonQuery): Result<Unit>

    suspend fun robotExists(robotQuery: RobotQuery): Boolean

    suspend fun robotExists(robot: Robot, season: Season, team: Team): Boolean


    suspend fun insertEvent(event: Event): Result<Unit>

    suspend fun eventExists(eventQuery: EventQuery): Boolean

    suspend fun eventExists(event: Event): Boolean

    suspend fun getEvents(): Result<List<Event>>

    suspend fun getEventsSimple(): Result<List<Event>>

    suspend fun getEvent(eventQuery: EventQuery): Result<Event>

    suspend fun getEventWithMatches(eventQuery: EventQuery): Result<Event>

    suspend fun getEventWithParticipants(eventQuery: EventQuery): Result<Event>


    suspend fun insertMatch(match: Match, eventQuery: EventQuery): Result<Unit>

    suspend fun matchExists(matchQuery: MatchQuery): Boolean

    suspend fun matchExists(match: Match, event: Event): Boolean

    suspend fun insertParticipant(participant: Participant, teamQuery: TeamQuery, matchQuery: MatchQuery): Result<Unit>

    suspend fun participantExists(participantQuery: ParticipantQuery): Boolean

    suspend fun participantExists(participant: Participant, match: Match, event: Event): Boolean

    suspend fun insertMetric(metric: Metric, participantQuery: ParticipantQuery): Result<Unit>

    suspend fun metricExists(metricQuery: MetricQuery): Boolean

    suspend fun metricExists(metric: Metric, participant: Participant, match: Match, event: Event): Boolean
}
