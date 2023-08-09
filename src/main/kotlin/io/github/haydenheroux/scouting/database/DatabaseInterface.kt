package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.event.EventQuery
import io.github.haydenheroux.scouting.models.event.EventReference
import io.github.haydenheroux.scouting.models.match.*
import io.github.haydenheroux.scouting.models.team.*

interface DatabaseInterface {
    suspend fun getTeams(): List<TeamReference>

    suspend fun getTeam(teamQuery: TeamQuery): TeamReference

    suspend fun getTeam(teamId: Int): TeamReference

    suspend fun getSeason(seasonQuery: SeasonQuery): SeasonReference

    suspend fun getSeason(seasonId: Int): SeasonReference

    suspend fun getSeasons(teamReference: TeamReference): List<SeasonReference>

    suspend fun getRobots(seasonReference: SeasonReference): List<RobotReference>

    suspend fun getRobot(robotQuery: RobotQuery): RobotReference

    suspend fun getRobot(robotId: Int): RobotReference

    suspend fun getEvents(): List<EventReference>

    suspend fun getEvents(seasonReference: SeasonReference): List<EventReference>

    suspend fun getEvent(eventQuery: EventQuery): EventReference

    suspend fun getEvent(eventId: Int): EventReference

    suspend fun getMatch(matchQuery: MatchQuery): MatchReference

    suspend fun getMatch(matchId: Int): MatchReference

    suspend fun getMatches(eventReference: EventReference): List<MatchReference>

    suspend fun getParticipant(participantQuery: ParticipantQuery): ParticipantReference

    suspend fun getParticipant(participantId: Int): ParticipantReference

    suspend fun getParticipants(matchReference: MatchReference): List<ParticipantReference>

    suspend fun getMetrics(participantReference: ParticipantReference): List<MetricReference>

    suspend fun insertTeam(team: Team)
    suspend fun insertSeason(season: Season, teamQuery: TeamQuery)
    suspend fun insertSeasonEvent(eventQuery: EventQuery, seasonQuery: SeasonQuery)
    suspend fun insertRobot(robot: Robot, seasonQuery: SeasonQuery)
    suspend fun insertEvent(event: Event)
    suspend fun insertMatch(match: Match, eventQuery: EventQuery)
    suspend fun insertParticipant(participant: Participant, matchQuery: MatchQuery, robotQuery: RobotQuery)
}
