package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.models.event.*
import io.github.haydenheroux.scouting.models.match.*
import io.github.haydenheroux.scouting.models.team.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseImplementation : DatabaseInterface {

    override suspend fun getTeams(): List<TeamReference> {
        return query {
            TeamTable.selectAll().map { it.teamReference() }
        }
    }

    override suspend fun getTeam(teamQuery: TeamQuery): TeamReference {
        return getTeamRow(teamQuery)!!.teamReference()
    }

    override suspend fun getTeam(teamId: Int): TeamReference {
        return getTeamRow(teamId)!!.teamReference()
    }

    private suspend fun getTeamRow(teamQuery: TeamQuery): ResultRow? {
        return query {
            TeamTable.select { TeamTable.number eq teamQuery.number }.singleOrNull()
        }
    }

    private suspend fun getTeamRow(teamId: Int): ResultRow? {
        return query {
            TeamTable.select { TeamTable.id eq teamId }.singleOrNull()
        }
    }

    private suspend fun getTeamId(teamQuery: TeamQuery): Int {
        return getTeamRow(teamQuery)!![TeamTable.id].value
    }

    private suspend fun teamExists(teamQuery: TeamQuery): Boolean {
        return getTeamRow(teamQuery)?.let { true } ?: false
    }

    private fun rowToTeamQuery(teamRow: ResultRow): TeamQuery {
        val teamNumber = teamRow[TeamTable.number]

        return TeamQuery(teamNumber)
    }

    override suspend fun getSeason(seasonQuery: SeasonQuery): SeasonReference {
        return getSeasonRow(seasonQuery)!!.seasonReference()
    }

    override suspend fun getSeason(seasonId: Int): SeasonReference {
        return getSeasonRow(seasonId)!!.seasonReference()
    }

    override suspend fun getSeasons(teamReference: TeamReference): List<SeasonReference> {
        return query {
            SeasonTable.select { SeasonTable.teamId eq teamReference.teamId }.map { it.seasonReference() }
        }
    }

    private suspend fun getSeasonRow(seasonQuery: SeasonQuery): ResultRow? {
        val teamId = getTeamId(seasonQuery.team)

        return query {
            SeasonTable.select { (SeasonTable.year eq seasonQuery.year) and (SeasonTable.teamId eq teamId) }
                .singleOrNull()
        }
    }

    private suspend fun getSeasonRow(seasonId: Int): ResultRow? {
        return query {
            SeasonTable.select { SeasonTable.id eq seasonId }.singleOrNull()
        }
    }

    private suspend fun getSeasonId(seasonQuery: SeasonQuery): Int {
        return getSeasonRow(seasonQuery)!![SeasonTable.id].value
    }

    private suspend fun seasonExists(seasonQuery: SeasonQuery): Boolean {
        return getSeasonRow(seasonQuery)?.let { true } ?: false
    }

    private suspend fun rowToSeasonQuery(seasonRow: ResultRow): SeasonQuery {
        val year = seasonRow[SeasonTable.year]

        val teamQuery = rowToTeamQuery(getTeamRow(seasonRow[SeasonTable.teamId].value)!!)

        return SeasonQuery(year, teamQuery)
    }

    override suspend fun getRobots(seasonReference: SeasonReference): List<RobotReference> {
        return query {
            RobotTable.select { RobotTable.seasonId eq seasonReference.seasonId }.map { it.robotReference() }
        }
    }

    override suspend fun getRobot(robotQuery: RobotQuery): RobotReference {
        return getRobotRow(robotQuery)!!.robotReference()
    }

    override suspend fun getRobot(robotId: Int): RobotReference {
        return getRobotRow(robotId)!!.robotReference()
    }

    private suspend fun getRobotRow(robotQuery: RobotQuery): ResultRow? {
        val seasonId = getSeasonId(robotQuery.season)

        return query {
            RobotTable.select { (RobotTable.name eq robotQuery.name) and (RobotTable.seasonId eq seasonId) }
                .singleOrNull()
        }
    }

    private suspend fun getRobotRow(robotId: Int): ResultRow? {
        return query {
            RobotTable.select { RobotTable.id eq robotId }.singleOrNull()
        }
    }

    private suspend fun getRobotId(robotQuery: RobotQuery): Int {
        return getRobotRow(robotQuery)!![RobotTable.id].value
    }

    private suspend fun robotExists(robotQuery: RobotQuery): Boolean {
        return getRobotRow(robotQuery)?.let { true } ?: false
    }

    private suspend fun rowToRobotQuery(robotRow: ResultRow): RobotQuery {
        val name = robotRow[RobotTable.name]

        val season = rowToSeasonQuery(getSeasonRow(robotRow[RobotTable.seasonId].value)!!)

        return RobotQuery(name, season)
    }

    override suspend fun getEvents(): List<EventReference> {
        return query {
            EventTable.selectAll().map { it.eventReference() }
        }
    }

    override suspend fun getEvents(seasonReference: SeasonReference): List<EventReference> {
        return query {
            SeasonEventTable.select { SeasonEventTable.seasonId eq seasonReference.seasonId }.map {seasonEventRow ->
                val eventId = seasonEventRow[SeasonEventTable.eventId].value

                getEvent(eventId)
            }
        }
    }

    override suspend fun getEvent(eventQuery: EventQuery): EventReference {
        return getEventRow(eventQuery)!!.eventReference()
    }

    override suspend fun getEvent(eventId: Int): EventReference {
        return getEventRow(eventId)!!.eventReference()
    }

    private suspend fun getEventRow(eventQuery: EventQuery): ResultRow? {
        return query {
            EventTable.select { (EventTable.name eq eventQuery.name) and (EventTable.region eq eventQuery.region) and (EventTable.year eq eventQuery.year) and (EventTable.week eq eventQuery.week) }
                .singleOrNull()
        }
    }

    private suspend fun getEventRow(eventId: Int): ResultRow? {
        return query {
            EventTable.select { EventTable.id eq eventId }.singleOrNull()
        }
    }

    private suspend fun getEventId(eventQuery: EventQuery): Int {
        return getEventRow(eventQuery)!![EventTable.id].value
    }

    private suspend fun eventExists(eventQuery: EventQuery): Boolean {
        return getEventRow(eventQuery)?.let { true } ?: false
    }

    private fun rowToEventQuery(eventRow: ResultRow): EventQuery {
        val name = eventRow[EventTable.name]
        val region = eventRow[EventTable.region]
        val year = eventRow[EventTable.year]
        val week = eventRow[EventTable.week]

        return EventQuery(name, region, year, week)
    }

    override suspend fun getMatch(matchQuery: MatchQuery): MatchReference {
        return getMatchRow(matchQuery)!!.matchReference()
    }

    override suspend fun getMatch(matchId: Int): MatchReference {
        return getMatchRow(matchId)!!.matchReference()
    }

    override suspend fun getMatches(eventReference: EventReference): List<MatchReference> {
        return query {
            MatchTable.select { MatchTable.eventId eq eventReference.eventId }.map { it.matchReference() }
        }
    }

    private suspend fun getMatchRow(matchQuery: MatchQuery): ResultRow? {
        val eventId = getEventId(matchQuery.event)

        return query {
            MatchTable.select { (MatchTable.eventId eq eventId) and (MatchTable.number eq matchQuery.number) }
                .singleOrNull()
        }
    }

    private suspend fun getMatchRow(matchId: Int): ResultRow? {
        return query {
            MatchTable.select { MatchTable.id eq matchId }.singleOrNull()
        }
    }

    private suspend fun getMatchId(matchQuery: MatchQuery): Int {
        return getMatchRow(matchQuery)!![MatchTable.id].value
    }

    private suspend fun matchExists(matchQuery: MatchQuery): Boolean {
        return getMatchRow(matchQuery)?.let { true } ?: false
    }

    private suspend fun rowToMatchQuery(matchRow: ResultRow): MatchQuery {
        val matchNumber = matchRow[MatchTable.number]

        val eventQuery = rowToEventQuery(getEventRow(matchRow[MatchTable.eventId].value)!!)

        return MatchQuery(matchNumber, eventQuery)
    }

    override suspend fun getParticipant(participantQuery: ParticipantQuery): ParticipantReference {
        return getParticipantRow(participantQuery)!!.participantReference()
    }

    override suspend fun getParticipant(participantId: Int): ParticipantReference {
        return getParticipantRow(participantId)!!.participantReference()
    }

    override suspend fun getParticipants(matchReference: MatchReference): List<ParticipantReference> {
        return query {
            ParticipantTable.select { ParticipantTable.matchId eq matchReference.matchId }.map { it.participantReference() }
        }
    }

    private suspend fun getParticipantRow(participantQuery: ParticipantQuery): ResultRow? {
        val matchId = getMatchId(participantQuery.match)
        val robotId = getRobotId(participantQuery.robot)

        return query {
            ParticipantTable.select { (ParticipantTable.matchId eq matchId) and (ParticipantTable.robotId eq robotId) }
                .singleOrNull()
        }
    }

    private suspend fun getParticipantRow(participantId: Int): ResultRow? {
        return query {
            ParticipantTable.select { ParticipantTable.id eq participantId }.singleOrNull()
        }
    }

    private suspend fun getParticipantId(participantQuery: ParticipantQuery): Int {
        return getParticipantRow(participantQuery)!![ParticipantTable.id].value
    }

    private suspend fun participantExists(participantQuery: ParticipantQuery): Boolean {
        return getParticipantRow(participantQuery)?.let { true } ?: false
    }

    private suspend fun rowToParticipantQuery(participantRow: ResultRow): ParticipantQuery {
        val match = rowToMatchQuery(getMatchRow(participantRow[ParticipantTable.matchId].value)!!)
        val robot = rowToRobotQuery(getRobotRow(participantRow[ParticipantTable.robotId].value)!!)

        return ParticipantQuery(match, robot)
    }

    override suspend fun getMetrics(participantReference: ParticipantReference): List<MetricReference> {
        return query {
            MetricTable.select { MetricTable.participantId eq participantReference.participantId }.map { it.metricReference() }
        }
    }

    override suspend fun insertTeam(team: Team) {
        if (teamExists(team.query())) throw Exception("Team exists")

        transaction {
            TeamTable.insert {
                it[number] = team.number
                it[name] = team.name
                it[region] = team.region
            }
        }

        for (season in team.seasons) {
            insertSeason(season, team.query())
        }
    }

    override suspend fun insertSeason(season: Season, teamQuery: TeamQuery) {
        if (seasonExists(season.query(teamQuery))) throw Exception("Season exists")

        val teamId = getTeamId(teamQuery)

        transaction {
            SeasonTable.insert {
                it[this.teamId] = teamId
                it[year] = season.year
            }
        }

        for (event in season.events) {
            insertSeasonEvent(event.query(), season.query(teamQuery))
        }

        for (robot in season.robots) {
            insertRobot(robot, season.query(teamQuery))
        }
    }

    override suspend fun insertSeasonEvent(eventQuery: EventQuery, seasonQuery: SeasonQuery) {
        // TODO Test cases

        val seasonId = getSeasonId(seasonQuery)
        val eventId = getEventId(eventQuery)

        transaction {
            SeasonEventTable.insert {
                it[this.seasonId] = seasonId
                it[this.eventId] = eventId
            }
        }
    }

    override suspend fun insertRobot(robot: Robot, seasonQuery: SeasonQuery) {
        if (robotExists(robot.query(seasonQuery))) throw Exception("Robot exists")

        val seasonId = getSeasonId(seasonQuery)

        transaction {
            RobotTable.insert {
                it[this.seasonId] = seasonId
                it[name] = robot.name
            }
        }
    }

    override suspend fun insertEvent(event: Event) {
        if (eventExists(event.query())) throw Exception("Event exists")

        transaction {
            EventTable.insert {
                it[name] = event.name
                it[region] = event.region
                it[year] = event.year
                it[week] = event.week
            }
        }

        for (match in event.matches) {
            insertMatch(match, event.query())
        }
    }

    override suspend fun insertMatch(match: Match, eventQuery: EventQuery) {
        if (matchExists(match.query(eventQuery))) throw Exception("Match exists")

        val eventId = getEventId(eventQuery)

        transaction {
            MatchTable.insert {
                it[this.eventId] = eventId
                it[number] = match.number
                it[type] = match.type
            }
        }

        for (participant in match.participants) {
            // TODO
        }
    }

    override suspend fun insertParticipant(participant: Participant, matchQuery: MatchQuery, robotQuery: RobotQuery) {
        // TODO Duplicate participants inserted
        if (participantExists(ParticipantQuery(matchQuery, robotQuery))) throw Exception("Participant exists")

        val matchId = getMatchId(matchQuery)
        val robotId = getRobotId(robotQuery)

        transaction {
            val participantId = ParticipantTable.insertAndGetId {
                it[this.matchId] = matchId
                it[this.robotId] = robotId
                it[alliance] = participant.alliance
            }

            for (metric in participant.metrics) {
                MetricTable.insert {
                    it[MetricTable.participantId] = participantId
                    it[key] = metric.key
                    it[value] = metric.value
                }
            }
        }

    }
}

val db = DatabaseImplementation()
