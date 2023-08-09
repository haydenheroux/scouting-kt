package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.models.event.*
import io.github.haydenheroux.scouting.models.match.*
import io.github.haydenheroux.scouting.models.team.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseImplementation : DatabaseInterface {

    override suspend fun getTeams(): List<TeamData> {
        return query {
            TeamTable.selectAll().map { teamRow -> TeamData.from(teamRow) }
        }
    }

    override suspend fun getTeamByQuery(teamQuery: TeamQuery): TeamData {
        val teamRow = getTeamRow(teamQuery)!!
        return TeamData.from(teamRow)
    }

    override suspend fun getTeamBySeason(seasonData: SeasonData): TeamData {
        return query {
            val seasonRow = SeasonTable.select { SeasonTable.id eq seasonData.seasonId }.single()
            val teamId = seasonRow[SeasonTable.teamId].value

            getTeamById(teamId)
        }
    }

    override suspend fun getTeamById(teamId: Int): TeamData {
        val teamRow = getTeamRow(teamId)!!
        return TeamData.from(teamRow)
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

    override suspend fun getSeasonByQuery(seasonQuery: SeasonQuery): SeasonData {
        val seasonRow = getSeasonRow(seasonQuery)!!
        return SeasonData.from(seasonRow)
    }

    override suspend fun getSeasonByRobot(robotData: RobotData): SeasonData {
        return query {
            val robotRow = RobotTable.select { RobotTable.id eq robotData.robotId }.single()
            val seasonId = robotRow[RobotTable.seasonId].value

            getSeasonById(seasonId)
        }
    }

    override suspend fun getSeasonById(seasonId: Int): SeasonData {
        val seasonRow = getSeasonRow(seasonId)!!
        return SeasonData.from(seasonRow)
    }

    override suspend fun getSeasonsByTeam(teamData: TeamData): List<SeasonData> {
        return query {
            SeasonTable.select { SeasonTable.teamId eq teamData.teamId }.map { seasonRow -> SeasonData.from(seasonRow) }
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

    override suspend fun getRobotsBySeason(seasonData: SeasonData): List<RobotData> {
        return query {
            RobotTable.select { RobotTable.seasonId eq seasonData.seasonId }
                .map { robotRow -> RobotData.from(robotRow) }
        }
    }

    override suspend fun getRobotByQuery(robotQuery: RobotQuery): RobotData {
        val robotRow = getRobotRow(robotQuery)!!
        return RobotData.from(robotRow)
    }

    override suspend fun getRobotById(robotId: Int): RobotData {
        val robotRow = getRobotRow(robotId)!!
        return RobotData.from(robotRow)
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

    override suspend fun getEvents(): List<EventData> {
        return query {
            EventTable.selectAll().map { eventRow -> EventData.from(eventRow) }
        }
    }

    override suspend fun getEventsBySeason(seasonData: SeasonData): List<EventData> {
        return query {
            SeasonEventTable.select { SeasonEventTable.seasonId eq seasonData.seasonId }.map { seasonEventRow ->
                val eventId = seasonEventRow[SeasonEventTable.eventId].value

                getEventById(eventId)
            }
        }
    }

    override suspend fun getEventByMatch(matchData: MatchData): EventData {
        return query {
            val matchRow = MatchTable.select { MatchTable.id eq matchData.matchId }.single()
            val eventId = matchRow[MatchTable.eventId].value

            getEventById(eventId)
        }
    }

    override suspend fun getEventByQuery(eventQuery: EventQuery): EventData {
        val eventRow = getEventRow(eventQuery)!!
        return EventData.from(eventRow)
    }

    override suspend fun getEventById(eventId: Int): EventData {
        val eventRow = getEventRow(eventId)!!
        return EventData.from(eventRow)
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

    override suspend fun getMatchByQuery(matchQuery: MatchQuery): MatchData {
        val matchRow = getMatchRow(matchQuery)!!
        return MatchData.from(matchRow)
    }

    override suspend fun getMatchByParticipant(participantData: ParticipantData): MatchData {
        return query {
            val participantRow =
                ParticipantTable.select { ParticipantTable.id eq participantData.participantId }.single()
            val matchId = participantRow[ParticipantTable.matchId].value

            getMatchById(matchId)
        }
    }

    override suspend fun getMatchById(matchId: Int): MatchData {
        val matchRow = getMatchRow(matchId)!!
        return MatchData.from(matchRow)
    }

    override suspend fun getMatchesByEvent(eventData: EventData): List<MatchData> {
        return query {
            MatchTable.select { MatchTable.eventId eq eventData.eventId }.map { matchRow -> MatchData.from(matchRow) }
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

    override suspend fun getParticipantByQuery(participantQuery: ParticipantQuery): ParticipantData {
        val participantRow = getParticipantRow(participantQuery)!!
        return ParticipantData.from(participantRow)
    }

    override suspend fun getParticipantByMetric(metricData: MetricData): ParticipantData {
        return query {
            val metricRow = MetricTable.select { MetricTable.id eq metricData.metricId }.single()
            val participantId = metricRow[MetricTable.participantId].value

            getParticipantById(participantId)
        }
    }

    override suspend fun getParticipantById(participantId: Int): ParticipantData {
        val participantRow = getParticipantRow(participantId)!!
        return ParticipantData.from(participantRow)
    }

    override suspend fun getParticipantsByMatch(matchData: MatchData): List<ParticipantData> {
        return query {
            ParticipantTable.select { ParticipantTable.matchId eq matchData.matchId }
                .map { participantRow -> ParticipantData.from(participantRow) }
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

    override suspend fun getMetricsByParticipant(participantData: ParticipantData): List<MetricData> {
        return query {
            MetricTable.select { MetricTable.participantId eq participantData.participantId }
                .map { metricRow -> MetricData.from(metricRow) }
        }
    }

    override suspend fun insertTeam(teamDTO: TeamDTO) {
        val teamQuery = teamQueryOf(teamDTO)

        if (teamExists(teamQuery)) throw Exception("Team exists")

        transaction {
            TeamTable.insert {
                it[number] = teamDTO.number
                it[name] = teamDTO.name
                it[region] = teamDTO.region
            }
        }

        for (season in teamDTO.seasons) {
            insertSeason(season, teamQuery)
        }
    }

    override suspend fun insertSeason(seasonDTO: SeasonDTO, teamQuery: TeamQuery) {
        val seasonQuery = seasonQueryOf(seasonDTO, teamQuery)

        if (seasonExists(seasonQuery)) throw Exception("Season exists")

        val teamId = getTeamId(teamQuery)

        transaction {
            SeasonTable.insert {
                it[this.teamId] = teamId
                it[year] = seasonDTO.year
            }
        }


        for (event in seasonDTO.events) {
            val eventQuery = eventQueryOf(event)
            insertSeasonEvent(eventQuery, seasonQuery)
        }

        for (robot in seasonDTO.robots) {
            insertRobot(robot, seasonQuery)
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

    override suspend fun insertRobot(robotDTO: RobotDTO, seasonQuery: SeasonQuery) {
        val robotQuery = robotQueryOf(robotDTO, seasonQuery)

        if (robotExists(robotQuery)) throw Exception("Robot exists")

        val seasonId = getSeasonId(seasonQuery)

        transaction {
            RobotTable.insert {
                it[this.seasonId] = seasonId
                it[name] = robotDTO.name
            }
        }
    }

    override suspend fun insertEvent(eventDTO: EventDTO) {
        val eventQuery = eventQueryOf(eventDTO)

        if (eventExists(eventQuery)) throw Exception("Event exists")

        transaction {
            EventTable.insert {
                it[name] = eventDTO.name
                it[region] = eventDTO.region
                it[year] = eventDTO.year
                it[week] = eventDTO.week
            }
        }

        for (match in eventDTO.matches) {
            insertMatch(match, eventQuery)
        }
    }

    override suspend fun insertMatch(matchDTO: MatchDTO, eventQuery: EventQuery) {
        val matchQuery = matchQueryOf(matchDTO, eventQuery)

        if (matchExists(matchQuery)) throw Exception("Match exists")

        val eventId = getEventId(eventQuery)

        transaction {
            MatchTable.insert {
                it[this.eventId] = eventId
                it[number] = matchDTO.number
                it[type] = matchDTO.type
            }
        }

        for (participant in matchDTO.participants) {
            // TODO
        }
    }

    override suspend fun insertParticipant(
        participantDTO: ParticipantDTO,
        matchQuery: MatchQuery,
        robotQuery: RobotQuery
    ) {
        // TODO Duplicate participants inserted
        val participantQuery = participantQueryOf(matchQuery, robotQuery)
        if (participantExists(participantQuery)) throw Exception("Participant exists")

        val matchId = getMatchId(matchQuery)
        val robotId = getRobotId(robotQuery)

        transaction {
            val participantId = ParticipantTable.insertAndGetId {
                it[this.matchId] = matchId
                it[this.robotId] = robotId
                it[alliance] = participantDTO.alliance
            }

            for (metric in participantDTO.metrics) {
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
