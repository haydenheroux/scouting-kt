package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.errors.DatabaseError
import io.github.haydenheroux.scouting.errors.Either
import io.github.haydenheroux.scouting.models.*

interface DatabaseInterface {
    suspend fun insertTeam(team: Team): Either<Unit, DatabaseError>

    suspend fun teamExists(teamQuery: TeamQuery): Boolean

    suspend fun teamExists(team: Team): Boolean

    suspend fun getTeams(): Either<List<Team>, DatabaseError>

    suspend fun getTeamsSimple(): Either<List<Team>, DatabaseError>

    suspend fun getTeam(teamQuery: TeamQuery): Either<Team, DatabaseError>

    suspend fun getTeamWithEvents(teamQuery: TeamQuery): Either<Team, DatabaseError>

    suspend fun getTeamWithMatches(teamQuery: TeamQuery): Either<Team, DatabaseError>


    suspend fun insertSeason(season: Season, teamQuery: TeamQuery): Either<Unit, DatabaseError>

    suspend fun insertSeasonEvent(eventQuery: EventQuery, seasonQuery: SeasonQuery): Either<Unit, DatabaseError>

    suspend fun seasonExists(seasonQuery: SeasonQuery): Boolean

    suspend fun seasonExists(season: Season, team: Team): Boolean

    suspend fun getSeason(seasonQuery: SeasonQuery): Either<Season, DatabaseError>

    suspend fun getSeasonWithEventsAndTeam(seasonQuery: SeasonQuery): Either<Pair<Season, Team>, DatabaseError>

    suspend fun getSeasonWithMatchesAndTeam(seasonQuery: SeasonQuery): Either<Pair<Season, Team>, DatabaseError>


    suspend fun insertRobot(robot: Robot, seasonQuery: SeasonQuery): Either<Unit, DatabaseError>

    suspend fun robotExists(robotQuery: RobotQuery): Boolean

    suspend fun robotExists(robot: Robot, season: Season, team: Team): Boolean

    suspend fun getRobot(robotQuery: RobotQuery): Either<Robot, DatabaseError>


    suspend fun insertEvent(event: Event): Either<Unit, DatabaseError>

    suspend fun eventExists(eventQuery: EventQuery): Boolean

    suspend fun eventExists(event: Event): Boolean

    suspend fun getEvents(): Either<List<Event>, DatabaseError>

    suspend fun getEventsSimple(): Either<List<Event>, DatabaseError>

    suspend fun getEvent(eventQuery: EventQuery): Either<Event, DatabaseError>

    suspend fun getEventWithMatches(eventQuery: EventQuery): Either<Event, DatabaseError>

    suspend fun getEventWithTeamNumbers(eventQuery: EventQuery): Either<Event, DatabaseError>


    suspend fun insertMatch(match: Match, eventQuery: EventQuery): Either<Unit, DatabaseError>

    suspend fun matchExists(matchQuery: MatchQuery): Boolean

    suspend fun matchExists(match: Match, event: Event): Boolean

    suspend fun getMatch(matchQuery: MatchQuery): Either<Match, DatabaseError>

    suspend fun getMatchWithMetricsAndEvent(matchQuery: MatchQuery): Either<Pair<Match, Event>, DatabaseError>


    suspend fun insertParticipant(participant: Participant, matchQuery: MatchQuery): Either<Unit, DatabaseError>

    suspend fun participantExists(participantQuery: ParticipantQuery): Boolean

    suspend fun participantExists(participant: Participant, match: Match, event: Event): Boolean

    suspend fun getParticipant(participantQuery: ParticipantQuery): Either<Participant, DatabaseError>


    suspend fun insertMetric(metric: Metric, participantQuery: ParticipantQuery): Either<Unit, DatabaseError>

    suspend fun metricExists(metricQuery: MetricQuery): Boolean

    suspend fun metricExists(metric: Metric, participant: Participant, match: Match, event: Event): Boolean

    suspend fun getMetric(metricQuery: MetricQuery): Either<Metric, DatabaseError>
}
