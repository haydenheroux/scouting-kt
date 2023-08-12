package io.github.haydenheroux.scouting.database.sql

import io.github.haydenheroux.scouting.database.DatabaseInterface
import io.github.haydenheroux.scouting.database.sql.tables.*
import io.github.haydenheroux.scouting.models.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object SQLDatabase : DatabaseInterface {
    fun init() {
        val url = "jdbc:sqlite:./build/db"
        val driver = "org.sqlite.JDBC"
        transaction(Database.connect(url, driver)) {
            SchemaUtils.create(
                TeamTable,
                SeasonTable,
                RobotTable,
                ParticipantTable,
                MetricTable,
                MatchTable,
                EventTable,
                SeasonEventTable
            )
        }
    }

    private suspend fun <T> query(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }


    override suspend fun getTeams(): Result<List<TeamNode>> {
        return runCatching {
            query {
                TeamTable.selectAll().map { teamRow -> TeamNode.from(teamRow) }
            }
        }
    }

    override suspend fun getTeamByQuery(teamQuery: TeamQuery): Result<TeamNode> {
        return runCatching {
            val teamRow = getTeamRow(teamQuery)!!
            TeamNode.from(teamRow)
        }
    }

    override suspend fun getTeamBySeason(seasonData: SeasonNode): Result<TeamNode> {
        return query {
            val seasonRow = SeasonTable.select { SeasonTable.id eq seasonData.id }.single()
            val teamId = seasonRow[SeasonTable.teamId].value

            getTeamById(teamId)
        }
    }

    override suspend fun getTeamByParticipant(participantData: ParticipantNode): Result<TeamNode> {
        return query {
            val participantRow = ParticipantTable.select { ParticipantTable.id eq participantData.id }.single()
            val teamId = participantRow[ParticipantTable.teamId].value

            getTeamById(teamId)
        }
    }

    override suspend fun getTeamById(teamId: Int): Result<TeamNode> {
        return runCatching {
            val teamRow = getTeamRow(teamId)!!
            TeamNode.from(teamRow)
        }
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

    override suspend fun getSeasonByQuery(seasonQuery: SeasonQuery): Result<SeasonNode> {
        return runCatching {
            val seasonRow = getSeasonRow(seasonQuery)!!
            SeasonNode.from(seasonRow)
        }
    }

    override suspend fun getSeasonByRobot(robotData: RobotNode): Result<SeasonNode> {
        return query {
            val robotRow = RobotTable.select { RobotTable.id eq robotData.id }.single()
            val seasonId = robotRow[RobotTable.seasonId].value

            getSeasonById(seasonId)
        }
    }

    override suspend fun getSeasonById(seasonId: Int): Result<SeasonNode> {
        return runCatching {
            val seasonRow = getSeasonRow(seasonId)!!
            SeasonNode.from(seasonRow)
        }
    }

    override suspend fun getSeasonsByTeam(teamData: TeamNode): Result<List<SeasonNode>> {
        return runCatching {
            query {
                SeasonTable.select { SeasonTable.teamId eq teamData.id }.map { seasonRow -> SeasonNode.from(seasonRow) }
            }
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

    override suspend fun getRobotsBySeason(seasonData: SeasonNode): Result<List<RobotNode>> {
        return runCatching {
            query {
                RobotTable.select { RobotTable.seasonId eq seasonData.id }
                    .map { robotRow -> RobotNode.from(robotRow) }
            }
        }
    }

    override suspend fun getRobotByQuery(robotQuery: RobotQuery): Result<RobotNode> {
        return runCatching {
            val robotRow = getRobotRow(robotQuery)!!
            RobotNode.from(robotRow)
        }
    }

    override suspend fun getRobotById(robotId: Int): Result<RobotNode> {
        return runCatching {
            val robotRow = getRobotRow(robotId)!!
            RobotNode.from(robotRow)
        }
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

    override suspend fun getEvents(): Result<List<EventNode>> {
        return runCatching {
            query {
                EventTable.selectAll().map { eventRow -> EventNode.from(eventRow) }
            }
        }
    }

    override suspend fun getEventsBySeason(seasonData: SeasonNode): Result<List<EventNode>> {
        return runCatching {
            query {
                SeasonEventTable.select { SeasonEventTable.seasonId eq seasonData.id }.map { seasonEventRow ->
                    val eventId = seasonEventRow[SeasonEventTable.eventId].value

                    getEventById(eventId).getOrThrow()
                }
            }
        }
    }

    override suspend fun getEventByMatch(matchData: MatchNode): Result<EventNode> {
        return query {
            val matchRow = MatchTable.select { MatchTable.id eq matchData.id }.single()
            val eventId = matchRow[MatchTable.eventId].value

            getEventById(eventId)
        }
    }

    override suspend fun getEventByQuery(eventQuery: EventQuery): Result<EventNode> {
        return runCatching {
            val eventRow = getEventRow(eventQuery)!!
            EventNode.from(eventRow)
        }
    }

    override suspend fun getEventById(eventId: Int): Result<EventNode> {
        return runCatching {
            val eventRow = getEventRow(eventId)!!
            EventNode.from(eventRow)
        }
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

    override suspend fun getMatchByQuery(matchQuery: MatchQuery): Result<MatchNode> {
        return runCatching {
            val matchRow = getMatchRow(matchQuery)!!
            MatchNode.from(matchRow)
        }
    }

    override suspend fun getMatchByParticipant(participantData: ParticipantNode): Result<MatchNode> {
        return query {
            val participantRow =
                ParticipantTable.select { ParticipantTable.id eq participantData.id }.single()
            val matchId = participantRow[ParticipantTable.matchId].value

            getMatchById(matchId)
        }
    }

    override suspend fun getMatchById(matchId: Int): Result<MatchNode> {
        return runCatching {
            val matchRow = getMatchRow(matchId)!!
            MatchNode.from(matchRow)
        }
    }

    override suspend fun getMatchesByEvent(eventData: EventNode): Result<List<MatchNode>> {
        return runCatching {
            query {
                MatchTable.select { MatchTable.eventId eq eventData.id }.map { matchRow -> MatchNode.from(matchRow) }
            }
        }
    }

    private suspend fun getMatchRow(matchQuery: MatchQuery): ResultRow? {
        val eventId = getEventId(matchQuery.event)

        return query {
            MatchTable.select { (MatchTable.eventId eq eventId) and (MatchTable.set eq matchQuery.set) and (MatchTable.number eq matchQuery.number) and (MatchTable.type eq matchQuery.type) }
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

    override suspend fun getParticipantByQuery(participantQuery: ParticipantQuery): Result<ParticipantNode> {
        return runCatching {
            val participantRow = getParticipantRow(participantQuery)!!
            ParticipantNode.from(participantRow)
        }
    }

    override suspend fun getParticipantByMetric(metricData: MetricNode): Result<ParticipantNode> {
        return query {
            val metricRow = MetricTable.select { MetricTable.id eq metricData.id }.single()
            val participantId = metricRow[MetricTable.participantId].value

            getParticipantById(participantId)
        }
    }

    override suspend fun getParticipantById(participantId: Int): Result<ParticipantNode> {
        return runCatching {
            val participantRow = getParticipantRow(participantId)!!
            ParticipantNode.from(participantRow)
        }
    }

    override suspend fun getParticipantsByMatch(matchData: MatchNode): Result<List<ParticipantNode>> {
        return runCatching {
            query {
                ParticipantTable.select { ParticipantTable.matchId eq matchData.id }
                    .map { participantRow -> ParticipantNode.from(participantRow) }
            }
        }
    }

    private suspend fun getParticipantRow(participantQuery: ParticipantQuery): ResultRow? {
        val teamId = getTeamId(participantQuery.team)
        val matchId = getMatchId(participantQuery.match)

        return query {
            ParticipantTable.select { (ParticipantTable.teamId eq teamId) and (ParticipantTable.matchId eq matchId) }
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

    override suspend fun getMetricsByParticipant(participantData: ParticipantNode): Result<List<MetricNode>> {
        return runCatching {
            query {
                MetricTable.select { MetricTable.participantId eq participantData.id }
                    .map { metricRow -> MetricNode.from(metricRow) }
            }
        }
    }

    override suspend fun insertTeam(team: Team): Result<Unit> {
        val teamQuery = teamQueryOf(team)

        if (teamExists(teamQuery)) return Result.failure(Exception("Team exists"))

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

        return Result.success(Unit)
    }

    override suspend fun insertSeason(season: Season, teamQuery: TeamQuery): Result<Unit> {
        val seasonQuery = seasonQueryOf(season, teamQuery)

        if (seasonExists(seasonQuery)) return Result.failure(Exception("Season exists"))

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

        return Result.success(Unit)
    }

    override suspend fun insertSeasonEvent(eventQuery: EventQuery, seasonQuery: SeasonQuery): Result<Unit> {
        if (!eventExists(eventQuery)) return Result.failure(Exception("Event does not exist"))
        if (!seasonExists(seasonQuery)) return Result.failure(Exception("Season does not exist"))

        val seasonId = getSeasonId(seasonQuery)
        val eventId = getEventId(eventQuery)

        transaction {
            SeasonEventTable.insert {
                it[this.seasonId] = seasonId
                it[this.eventId] = eventId
            }
        }

        return Result.success(Unit)
    }

    override suspend fun insertRobot(robot: Robot, seasonQuery: SeasonQuery): Result<Unit> {
        val robotQuery = robotQueryOf(robot, seasonQuery)

        if (robotExists(robotQuery)) return Result.failure(Exception("Robot exists"))

        val seasonId = getSeasonId(seasonQuery)

        transaction {
            RobotTable.insert {
                it[this.seasonId] = seasonId
                it[name] = robot.name
            }
        }

        return Result.success(Unit)
    }

    override suspend fun insertEvent(event: Event): Result<Unit> {
        val eventQuery = eventQueryOf(event)

        if (eventExists(eventQuery)) return Result.failure(Exception("Event exists"))

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

        return Result.success(Unit)
    }

    override suspend fun insertMatch(match: Match, eventQuery: EventQuery): Result<Unit> {
        val matchQuery = matchQueryOf(match, eventQuery)

        if (matchExists(matchQuery)) return Result.failure(Exception("Match exists"))

        val eventId = getEventId(eventQuery)

        transaction {
            MatchTable.insert {
                it[this.eventId] = eventId
                it[set] = match.set
                it[number] = match.number
                it[type] = match.type
            }
        }

        for (participant in match.participants) {
            // TODO
        }

        return Result.success(Unit)
    }

    override suspend fun insertParticipant(
        participant: Participant,
        teamQuery: TeamQuery,
        matchQuery: MatchQuery,
    ): Result<Unit> {
        val participantQuery = ParticipantQuery(teamQuery, matchQuery)

        if (participantExists(participantQuery)) return Result.failure(Exception("Participant exists"))

        val teamId = getTeamId(teamQuery)
        val matchId = getMatchId(matchQuery)

        transaction {
            val participantId = ParticipantTable.insertAndGetId {
                it[this.matchId] = matchId
                it[this.teamId] = teamId
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

        return Result.success(Unit)
    }

}