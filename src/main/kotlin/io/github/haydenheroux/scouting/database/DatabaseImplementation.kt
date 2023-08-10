package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.models.event.*
import io.github.haydenheroux.scouting.models.match.*
import io.github.haydenheroux.scouting.models.team.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseImplementation : DatabaseInterface {

    override suspend fun getTeams(): List<TeamNode> {
        return query {
            TeamTable.selectAll().map { teamRow -> TeamNode.from(teamRow) }
        }
    }

    override suspend fun getTeamByQuery(teamQuery: TeamQuery): TeamNode {
        val teamRow = getTeamRow(teamQuery)!!
        return TeamNode.from(teamRow)
    }

    override suspend fun getTeamBySeason(seasonData: SeasonNode): TeamNode {
        return query {
            val seasonRow = SeasonTable.select { SeasonTable.id eq seasonData.id }.single()
            val teamId = seasonRow[SeasonTable.teamId].value

            getTeamById(teamId)
        }
    }

    override suspend fun getTeamById(teamId: Int): TeamNode {
        val teamRow = getTeamRow(teamId)!!
        return TeamNode.from(teamRow)
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

    override suspend fun getSeasonByQuery(seasonQuery: SeasonQuery): SeasonNode {
        val seasonRow = getSeasonRow(seasonQuery)!!
        return SeasonNode.from(seasonRow)
    }

    override suspend fun getSeasonByRobot(robotData: RobotNode): SeasonNode {
        return query {
            val robotRow = RobotTable.select { RobotTable.id eq robotData.id }.single()
            val seasonId = robotRow[RobotTable.seasonId].value

            getSeasonById(seasonId)
        }
    }

    override suspend fun getSeasonById(seasonId: Int): SeasonNode {
        val seasonRow = getSeasonRow(seasonId)!!
        return SeasonNode.from(seasonRow)
    }

    override suspend fun getSeasonsByTeam(teamData: TeamNode): List<SeasonNode> {
        return query {
            SeasonTable.select { SeasonTable.teamId eq teamData.id }.map { seasonRow -> SeasonNode.from(seasonRow) }
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

    override suspend fun getRobotsBySeason(seasonData: SeasonNode): List<RobotNode> {
        return query {
            RobotTable.select { RobotTable.seasonId eq seasonData.id }
                .map { robotRow -> RobotNode.from(robotRow) }
        }
    }

    override suspend fun getRobotByQuery(robotQuery: RobotQuery): RobotNode {
        val robotRow = getRobotRow(robotQuery)!!
        return RobotNode.from(robotRow)
    }

    override suspend fun getRobotById(robotId: Int): RobotNode {
        val robotRow = getRobotRow(robotId)!!
        return RobotNode.from(robotRow)
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

    override suspend fun getEvents(): List<EventNode> {
        return query {
            EventTable.selectAll().map { eventRow -> EventNode.from(eventRow) }
        }
    }

    override suspend fun getEventsBySeason(seasonData: SeasonNode): List<EventNode> {
        return query {
            SeasonEventTable.select { SeasonEventTable.seasonId eq seasonData.id }.map { seasonEventRow ->
                val eventId = seasonEventRow[SeasonEventTable.eventId].value

                getEventById(eventId)
            }
        }
    }

    override suspend fun getEventByMatch(matchData: MatchNode): EventNode {
        return query {
            val matchRow = MatchTable.select { MatchTable.id eq matchData.id }.single()
            val eventId = matchRow[MatchTable.eventId].value

            getEventById(eventId)
        }
    }

    override suspend fun getEventByQuery(eventQuery: EventQuery): EventNode {
        val eventRow = getEventRow(eventQuery)!!
        return EventNode.from(eventRow)
    }

    override suspend fun getEventById(eventId: Int): EventNode {
        val eventRow = getEventRow(eventId)!!
        return EventNode.from(eventRow)
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

    override suspend fun getMatchByQuery(matchQuery: MatchQuery): MatchNode {
        val matchRow = getMatchRow(matchQuery)!!
        return MatchNode.from(matchRow)
    }

    override suspend fun getMatchByParticipant(participantData: ParticipantNode): MatchNode {
        return query {
            val participantRow =
                ParticipantTable.select { ParticipantTable.id eq participantData.id }.single()
            val matchId = participantRow[ParticipantTable.matchId].value

            getMatchById(matchId)
        }
    }

    override suspend fun getMatchById(matchId: Int): MatchNode {
        val matchRow = getMatchRow(matchId)!!
        return MatchNode.from(matchRow)
    }

    override suspend fun getMatchesByEvent(eventData: EventNode): List<MatchNode> {
        return query {
            MatchTable.select { MatchTable.eventId eq eventData.id }.map { matchRow -> MatchNode.from(matchRow) }
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

    override suspend fun getParticipantByQuery(participantQuery: ParticipantQuery): ParticipantNode {
        val participantRow = getParticipantRow(participantQuery)!!
        return ParticipantNode.from(participantRow)
    }

    override suspend fun getParticipantByMetric(metricData: MetricNode): ParticipantNode {
        return query {
            val metricRow = MetricTable.select { MetricTable.id eq metricData.id }.single()
            val participantId = metricRow[MetricTable.participantId].value

            getParticipantById(participantId)
        }
    }

    override suspend fun getParticipantById(participantId: Int): ParticipantNode {
        val participantRow = getParticipantRow(participantId)!!
        return ParticipantNode.from(participantRow)
    }

    override suspend fun getParticipantsByMatch(matchData: MatchNode): List<ParticipantNode> {
        return query {
            ParticipantTable.select { ParticipantTable.matchId eq matchData.id }
                .map { participantRow -> ParticipantNode.from(participantRow) }
        }
    }

    private suspend fun getParticipantRow(participantQuery: ParticipantQuery): ResultRow? {
        val matchId = getMatchId(participantQuery.match)

        return query {
            ParticipantTable.select { (ParticipantTable.matchId eq matchId) and (ParticipantTable.teamNumber eq participantQuery.team.number) }
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

    override suspend fun getMetricsByParticipant(participantData: ParticipantNode): List<MetricNode> {
        return query {
            MetricTable.select { MetricTable.participantId eq participantData.id }
                .map { metricRow -> MetricNode.from(metricRow) }
        }
    }

    override suspend fun insertTeam(team: Team) {
        val teamQuery = teamQueryOf(team)

        if (teamExists(teamQuery)) throw Exception("Team exists")

        transaction {
            TeamTable.insert {
                it[number] = team.number
                it[name] = team.name
                it[region] = team.region
            }
        }

        for (season in team.seasons) {
            insertSeason(season, teamQuery)
        }
    }

    override suspend fun insertSeason(season: Season, teamQuery: TeamQuery) {
        val seasonQuery = seasonQueryOf(season, teamQuery)

        if (seasonExists(seasonQuery)) throw Exception("Season exists")

        val teamId = getTeamId(teamQuery)

        transaction {
            SeasonTable.insert {
                it[this.teamId] = teamId
                it[year] = season.year
            }
        }


        for (event in season.events) {
            val eventQuery = eventQueryOf(event)
            insertSeasonEvent(eventQuery, seasonQuery)
        }

        for (robot in season.robots) {
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

    override suspend fun insertRobot(robot: Robot, seasonQuery: SeasonQuery) {
        val robotQuery = robotQueryOf(robot, seasonQuery)

        if (robotExists(robotQuery)) throw Exception("Robot exists")

        val seasonId = getSeasonId(seasonQuery)

        transaction {
            RobotTable.insert {
                it[this.seasonId] = seasonId
                it[name] = robot.name
            }
        }
    }

    override suspend fun insertEvent(event: Event) {
        val eventQuery = eventQueryOf(event)

        if (eventExists(eventQuery)) throw Exception("Event exists")

        transaction {
            EventTable.insert {
                it[name] = event.name
                it[region] = event.region
                it[year] = event.year
                it[week] = event.week
            }
        }

        for (match in event.matches) {
            insertMatch(match, eventQuery)
        }
    }

    override suspend fun insertMatch(match: Match, eventQuery: EventQuery) {
        val matchQuery = matchQueryOf(match, eventQuery)

        if (matchExists(matchQuery)) throw Exception("Match exists")

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

    override suspend fun insertParticipant(
        participant: Participant,
        matchQuery: MatchQuery,
    ) {
        // TODO Duplicate participants inserted
        val participantQuery = participantQueryOf(matchQuery, TeamQuery(participant.teamNumber))
        if (participantExists(participantQuery)) throw Exception("Participant exists")

        val matchId = getMatchId(matchQuery)

        transaction {
            val participantId = ParticipantTable.insertAndGetId {
                it[this.matchId] = matchId
                it[alliance] = participant.alliance
                it[teamNumber] = participant.teamNumber
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
