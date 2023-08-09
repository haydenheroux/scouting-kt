package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.models.event.EventDTO
import io.github.haydenheroux.scouting.models.event.EventData
import io.github.haydenheroux.scouting.models.event.EventQuery
import io.github.haydenheroux.scouting.models.match.*
import io.github.haydenheroux.scouting.models.team.*

interface DatabaseInterface {
    suspend fun getTeams(): List<TeamData>

    suspend fun getTeamByQuery(teamQuery: TeamQuery): TeamData

    suspend fun getTeamBySeason(seasonData: SeasonData): TeamData

    suspend fun getTeamById(teamId: Int): TeamData

    suspend fun getSeasonByQuery(seasonQuery: SeasonQuery): SeasonData

    suspend fun getSeasonsByTeam(teamData: TeamData): List<SeasonData>

    suspend fun getSeasonByRobot(robotData: RobotData): SeasonData

    suspend fun getSeasonById(seasonId: Int): SeasonData

    suspend fun getRobotByQuery(robotQuery: RobotQuery): RobotData

    suspend fun getRobotsBySeason(seasonData: SeasonData): List<RobotData>

    suspend fun getRobotById(robotId: Int): RobotData

    suspend fun getEvents(): List<EventData>

    suspend fun getEventsBySeason(seasonData: SeasonData): List<EventData>

    suspend fun getEventByQuery(eventQuery: EventQuery): EventData

    suspend fun getEventByMatch(matchData: MatchData): EventData

    suspend fun getEventById(eventId: Int): EventData

    suspend fun getMatchesByEvent(eventData: EventData): List<MatchData>

    suspend fun getMatchByQuery(matchQuery: MatchQuery): MatchData

    suspend fun getMatchByParticipant(participantData: ParticipantData): MatchData

    suspend fun getMatchById(matchId: Int): MatchData

    suspend fun getParticipantsByMatch(matchData: MatchData): List<ParticipantData>

    suspend fun getParticipantByQuery(participantQuery: ParticipantQuery): ParticipantData

    suspend fun getParticipantByMetric(metricData: MetricData): ParticipantData

    suspend fun getParticipantById(participantId: Int): ParticipantData

    suspend fun getMetricsByParticipant(participantData: ParticipantData): List<MetricData>

    suspend fun insertTeam(teamDTO: TeamDTO)
    suspend fun insertSeason(seasonDTO: SeasonDTO, teamQuery: TeamQuery)
    suspend fun insertSeasonEvent(eventQuery: EventQuery, seasonQuery: SeasonQuery)
    suspend fun insertRobot(robotDTO: RobotDTO, seasonQuery: SeasonQuery)
    suspend fun insertEvent(eventDTO: EventDTO)
    suspend fun insertMatch(matchDTO: MatchDTO, eventQuery: EventQuery)
    suspend fun insertParticipant(participantDTO: ParticipantDTO, matchQuery: MatchQuery, robotQuery: RobotQuery)
}
