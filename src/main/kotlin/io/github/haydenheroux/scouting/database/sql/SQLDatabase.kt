package io.github.haydenheroux.scouting.database.sql

import io.github.haydenheroux.scouting.database.DatabaseInterface
import io.github.haydenheroux.scouting.database.sql.excludes.Exclude
import io.github.haydenheroux.scouting.database.sql.tables.*
import io.github.haydenheroux.scouting.errors.*
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
                ParticipantMetricTable,
                MetricTable,
                MatchTable,
                EventTable,
                SeasonEventTable,
                AllianceMetricTable,
                AllianceTable
            )
        }
    }

    private suspend fun <T> query(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }

    private suspend fun getTeamNodes(): Either<List<TeamNode>, DatabaseError> {
        val result = runCatching {
            query {
                TeamTable.selectAll().map { teamRow -> TeamNode.from(teamRow) }
            }
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }


    override suspend fun getTeams(): Either<List<Team>, DatabaseError> {
        return when (val teamNodesOrError = getTeamNodes()) {
            is Success -> Success(teamNodesOrError.value.map { teamNode ->
                teamNode.tree(false, emptyList()).subtree()
            })

            is Error -> teamNodesOrError
        }
    }

    override suspend fun getTeamsSimple(): Either<List<Team>, DatabaseError> {
        return when (val teamNodesOrError = getTeamNodes()) {
            is Success -> Success(teamNodesOrError.value.map { teamNode -> teamNode.leaf() })
            is Error -> teamNodesOrError
        }
    }

    private suspend fun getTeamNode(teamQuery: TeamQuery): Either<TeamNode, DatabaseError> {
        val result = runCatching {
            val teamRow = getTeamRow(teamQuery)!!
            TeamNode.from(teamRow)
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }

    override suspend fun getTeam(teamQuery: TeamQuery): Either<Team, DatabaseError> {
        return when (val teamNodeOrError = getTeamNode(teamQuery)) {
            is Success -> Success(teamNodeOrError.value.tree(false, emptyList()).subtree())
            is Error -> teamNodeOrError
        }
    }

    override suspend fun getTeamWithEvents(teamQuery: TeamQuery): Either<Team, DatabaseError> {
        return when (val teamNodeOrError = getTeamNode(teamQuery)) {
            is Success -> Success(
                teamNodeOrError.value.tree(false, emptyList())
                    .subtree(2, listOf(Exclude.SEASON_ROBOTS, Exclude.EVENT_MATCHES))
            )

            is Error -> teamNodeOrError
        }
    }

    suspend fun getTeamById(teamId: Int): Either<TeamNode, DatabaseError> {
        val result = runCatching {
            val teamRow = getTeamRow(teamId)!!
            TeamNode.from(teamRow)
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }

    private suspend fun getTeamRow(teamQuery: TeamQuery): ResultRow? {
        return query {
            TeamTable.select { TeamTable.number eq teamQuery.teamNumber }.singleOrNull()
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

    override suspend fun teamExists(teamQuery: TeamQuery): Boolean {
        return getTeamRow(teamQuery)?.let { true } ?: false
    }

    override suspend fun teamExists(team: Team): Boolean {
        return teamExists(teamQueryOf(team))
    }

    private suspend fun getSeasonNode(seasonQuery: SeasonQuery): Either<SeasonNode, DatabaseError> {
        val result = runCatching {
            val seasonRow = getSeasonRow(seasonQuery)!!
            SeasonNode.from(seasonRow)
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }

    override suspend fun getSeason(seasonQuery: SeasonQuery): Either<Season, DatabaseError> {
        return when (val seasonNodeOrError = getSeasonNode(seasonQuery)) {
            is Success -> Success(seasonNodeOrError.value.tree(false, emptyList()).subtree())
            is Error -> seasonNodeOrError
        }
    }

    override suspend fun getSeasonWithEventsAndTeam(seasonQuery: SeasonQuery): Either<Pair<Season, Team>, DatabaseError> {
        return when (val seasonNodeOrError = getSeasonNode(seasonQuery)) {
            is Success -> {
                val seasonTree = seasonNodeOrError.value.tree(true, emptyList())
                val season = seasonTree.subtree(1, emptyList())
                val team = seasonTree.team!!.leaf()
                Success(Pair(season, team))
            }

            is Error -> seasonNodeOrError
        }
    }

    override suspend fun getSeasonWithMatchesAndTeam(seasonQuery: SeasonQuery): Either<Pair<Season, Team>, DatabaseError> {
        return when (val seasonNodeOrError = getSeasonNode(seasonQuery)) {
            is Success -> {
                val seasonTree = seasonNodeOrError.value.tree(true, emptyList())
                val season = seasonTree.subtree(4, listOf(Exclude.ALLIANCE_METRICS, Exclude.PARTICIPANT_METRICS))
                val team = seasonTree.team!!.leaf()
                Success(Pair(season, team))
            }

            is Error -> seasonNodeOrError
        }
    }

    suspend fun getSeasonById(seasonId: Int): Either<SeasonNode, DatabaseError> {
        val result = runCatching {
            val seasonRow = getSeasonRow(seasonId)!!
            SeasonNode.from(seasonRow)
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }

    suspend fun getSeasonsByTeam(teamData: TeamNode): Either<List<SeasonNode>, DatabaseError> {
        val result = runCatching {
            query {
                SeasonTable.select { SeasonTable.teamId eq teamData.id }.map { seasonRow -> SeasonNode.from(seasonRow) }
            }
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }

    private suspend fun getSeasonRow(seasonQuery: SeasonQuery): ResultRow? {
        val teamId = getTeamId(seasonQuery.teamQuery)

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

    override suspend fun seasonExists(seasonQuery: SeasonQuery): Boolean {
        return getSeasonRow(seasonQuery)?.let { true } ?: false
    }

    suspend fun getRobotsBySeason(seasonData: SeasonNode): Either<List<RobotNode>, DatabaseError> {
        val result = runCatching {
            query {
                RobotTable.select { RobotTable.seasonId eq seasonData.id }
                    .map { robotRow -> RobotNode.from(robotRow) }
            }
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }

    private suspend fun getRobotNode(robotQuery: RobotQuery): Either<RobotNode, DatabaseError> {
        val result = runCatching {
            val robotRow = getRobotRow(robotQuery)!!
            RobotNode.from(robotRow)
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }

    override suspend fun getRobot(robotQuery: RobotQuery): Either<Robot, DatabaseError> {
        return when (val robotNodeOrError = getRobotNode(robotQuery)) {
            is Success -> Success(robotNodeOrError.value.tree(false, emptyList()).subtree())
            is Error -> robotNodeOrError
        }
    }

    private suspend fun getRobotRow(robotQuery: RobotQuery): ResultRow? {
        val seasonId = getSeasonId(robotQuery.seasonQuery)

        return query {
            RobotTable.select { (RobotTable.name eq robotQuery.name) and (RobotTable.seasonId eq seasonId) }
                .singleOrNull()
        }
    }

    override suspend fun robotExists(robotQuery: RobotQuery): Boolean {
        return getRobotRow(robotQuery)?.let { true } ?: false
    }

    private suspend fun getEventNodes(): Either<List<EventNode>, DatabaseError> {
        val result = runCatching {
            query {
                EventTable.selectAll().map { eventRow -> EventNode.from(eventRow) }
            }
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }

    override suspend fun getEvents(): Either<List<Event>, DatabaseError> {
        return when (val eventNodesOrError = getEventNodes()) {
            is Success -> Success(eventNodesOrError.value.map { eventNode ->
                eventNode.tree(false, emptyList()).subtree()
            })

            is Error -> eventNodesOrError
        }
    }

    override suspend fun getEventsSimple(): Either<List<Event>, DatabaseError> {
        return when (val eventNodesOrError = getEventNodes()) {
            is Success -> Success(eventNodesOrError.value.map { eventNode -> eventNode.leaf() })
            is Error -> eventNodesOrError
        }
    }

    private suspend fun getEventNode(eventQuery: EventQuery): Either<EventNode, DatabaseError> {
        val result = runCatching {
            val eventRow = getEventRow(eventQuery)!!
            EventNode.from(eventRow)
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }

    override suspend fun getEvent(eventQuery: EventQuery): Either<Event, DatabaseError> {
        return when (val eventNodeOrError = getEventNode(eventQuery)) {
            is Success -> Success(eventNodeOrError.value.tree(false, emptyList()).subtree())
            is Error -> eventNodeOrError
        }
    }

    override suspend fun getEventWithMatches(eventQuery: EventQuery): Either<Event, DatabaseError> {
        return when (val eventNodeOrError = getEventNode(eventQuery)) {
            is Success -> Success(eventNodeOrError.value.tree(false, emptyList()).subtree(1, emptyList()))
            is Error -> eventNodeOrError
        }
    }

    override suspend fun getEventWithTeamNumbers(eventQuery: EventQuery): Either<Event, DatabaseError> {
        return when (val eventNodeOrError = getEventNode(eventQuery)) {
            is Success -> Success(
                eventNodeOrError.value.tree(false, emptyList())
                    .subtree(3, listOf(Exclude.ALLIANCE_METRICS, Exclude.PARTICIPANT_METRICS))
            )

            is Error -> eventNodeOrError
        }
    }

    suspend fun getEventsBySeason(seasonData: SeasonNode): Either<List<EventNode>, DatabaseError> {
        val result = runCatching {
            query {
                SeasonEventTable.select { SeasonEventTable.seasonId eq seasonData.id }
            }
        }

        val seasonEventRows = result.getOrNull() ?: return Error(DatabaseUnknownError)

        val seasonEventNodeOrErrors = query {
            seasonEventRows.map { seasonEventRow ->
                val eventId = seasonEventRow[SeasonEventTable.eventId].value

                getEventById(eventId)
            }
        }

        return if (seasonEventNodeOrErrors.all { it is Success }) {
            Success(seasonEventNodeOrErrors.map { (it as Success).value })
        } else {
            seasonEventNodeOrErrors.first { it is Error } as Error
        }
    }

    suspend fun getEventById(eventId: Int): Either<EventNode, DatabaseError> {
        val result = runCatching {
            val eventRow = getEventRow(eventId)!!
            EventNode.from(eventRow)
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }

    private suspend fun getEventRow(eventQuery: EventQuery): ResultRow? {
        return query {
            EventTable.select { (EventTable.code eq eventQuery.code) }.singleOrNull()
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

    override suspend fun eventExists(eventQuery: EventQuery): Boolean {
        return getEventRow(eventQuery)?.let { true } ?: false
    }

    override suspend fun eventExists(event: Event): Boolean {
        return eventExists(eventQueryOf(event))
    }

    private suspend fun getMatchNode(matchQuery: MatchQuery): Either<MatchNode, DatabaseError> {
        val result = runCatching {
            val matchRow = getMatchRow(matchQuery)!!
            MatchNode.from(matchRow)
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }

    override suspend fun getMatch(matchQuery: MatchQuery): Either<Match, DatabaseError> {
        return when (val matchNodeOrError = getMatchNode(matchQuery)) {
            is Success -> Success(matchNodeOrError.value.tree(false, emptyList()).subtree())
            is Error -> matchNodeOrError
        }
    }

    override suspend fun getMatchWithMetricsAndEvent(matchQuery: MatchQuery): Either<Pair<Match, Event>, DatabaseError> {
        return when (val matchNodeOrError = getMatchNode(matchQuery)) {
            is Success -> {
                val matchTree = matchNodeOrError.value.tree(true, emptyList())
                val match = matchTree.subtree(3, emptyList())
                val event = matchTree.event!!.leaf()

                return Success(Pair(match, event))
            }

            is Error -> matchNodeOrError
        }
    }

    private suspend fun insertAlliance(alliance: Alliance, matchId: Int): Either<Unit, DatabaseError> {
        val allianceId = query {
            AllianceTable.insertAndGetId {
                it[this.matchId] = matchId
                it[color] = alliance.color
            }.value
        }

        insertAllianceMetrics(alliance.metrics, allianceId)

        for (participant in alliance.participants) {
            insertParticipant(participant, allianceId)
        }

        return Success(Unit)
    }

    override suspend fun insertAlliance(alliance: Alliance, matchQuery: MatchQuery): Either<Unit, DatabaseError> {
        if (allianceExists(AllianceQuery(matchQuery, alliance.color))) return Error(DatabaseThingExists("alliance"))

        val matchId = getMatchId(matchQuery)

        return insertAlliance(alliance, matchId)
    }

    override suspend fun allianceExists(allianceQuery: AllianceQuery): Boolean {
        return getAllianceRow(allianceQuery)?.let { true } ?: false
    }

    override suspend fun getAlliance(allianceQuery: AllianceQuery): Either<Alliance, DatabaseError> {
        return when (val allianceNodeOrError = getAllianceNode(allianceQuery)) {
            is Success -> Success(allianceNodeOrError.value.tree(false, emptyList()).subtree())
            is Error -> allianceNodeOrError
        }
    }

    suspend fun getMatchById(matchId: Int): Either<MatchNode, DatabaseError> {
        val result = runCatching {
            val matchRow = getMatchRow(matchId)!!
            MatchNode.from(matchRow)
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }

    suspend fun getParticipantsByAlliance(alliance: AllianceNode): Either<List<ParticipantNode>, DatabaseError> {
        val result = runCatching {
            query {
                ParticipantTable.select { ParticipantTable.allianceId eq alliance.id }
                    .map { participantRow -> ParticipantNode.from(participantRow) }
            }
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }

    suspend fun getMetricsByAlliance(alliance: AllianceNode): Either<List<MetricNode>, DatabaseError> {
        val result = runCatching {
            query {
                AllianceMetricTable.select { AllianceMetricTable.allianceId eq alliance.id }
            }
        }

        val allianceMetricRows = result.getOrNull() ?: return Error(DatabaseUnknownError)

        val allianceMetricNodeOrErrors = query {
            allianceMetricRows.map { allianceMetricRow ->
                val metricId = allianceMetricRow[AllianceMetricTable.metricId].value

                getMetricById(metricId)
            }
        }

        return if (allianceMetricNodeOrErrors.all { it is Success }) {
            Success(allianceMetricNodeOrErrors.map { (it as Success).value })
        } else {
            allianceMetricNodeOrErrors.first { it is Error } as Error
        }
    }

    suspend fun getMatchesByEvent(eventData: EventNode): Either<List<MatchNode>, DatabaseError> {
        val result = runCatching {
            query {
                MatchTable.select { MatchTable.eventId eq eventData.id }.map { matchRow -> MatchNode.from(matchRow) }
            }
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
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

    override suspend fun matchExists(matchQuery: MatchQuery): Boolean {
        return getMatchRow(matchQuery)?.let { true } ?: false
    }

    private suspend fun getParticipantNode(participantQuery: ParticipantQuery): Either<ParticipantNode, DatabaseError> {
        val result = runCatching {
            val participantRow = getParticipantRow(participantQuery)!!
            ParticipantNode.from(participantRow)
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }

    override suspend fun getParticipant(participantQuery: ParticipantQuery): Either<Participant, DatabaseError> {
        return when (val participantNodeOrError = getParticipantNode(participantQuery)) {
            is Success -> Success(participantNodeOrError.value.tree(false, emptyList()).subtree())
            is Error -> participantNodeOrError
        }
    }

    suspend fun getAlliancesByMatch(matchData: MatchNode): Either<List<AllianceNode>, DatabaseError> {
        val result = runCatching {
            query {
                AllianceTable.select { AllianceTable.matchId eq matchData.id }
                    .map { allianceRow -> AllianceNode.from(allianceRow) }
            }
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }

    private suspend fun getAllianceRow(allianceId: Int): ResultRow? {
        return query {
            AllianceTable.select { AllianceTable.id eq allianceId }.singleOrNull()
        }
    }

    private suspend fun getAllianceRow(allianceQuery: AllianceQuery): ResultRow? {
        val matchId = getMatchId(allianceQuery.matchQuery)

        return query {
            AllianceTable.select { (AllianceTable.matchId eq matchId) and (AllianceTable.color eq allianceQuery.color) }
                .singleOrNull()
        }
    }

    private suspend fun getAllianceNode(allianceQuery: AllianceQuery): Either<AllianceNode, DatabaseError> {
        val result = runCatching {
            val allianceNode = getAllianceRow(allianceQuery)!!
            AllianceNode.from(allianceNode)
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }

    suspend fun getAllianceById(allianceId: Int): Either<AllianceNode, DatabaseError> {
        val result = runCatching {
            val allianceNode = getAllianceRow(allianceId)!!
            AllianceNode.from(allianceNode)
        }

        return result.getOrNull()?.let { Success(it) } ?: Error(DatabaseUnknownError)
    }

    private suspend fun getAllianceId(allianceQuery: AllianceQuery): Int {
        return getAllianceRow(allianceQuery)!![AllianceTable.id].value
    }

    private suspend fun getParticipantRow(participantQuery: ParticipantQuery): ResultRow? {
        val teamQuery = participantQuery.teamQuery
        val allianceId = getAllianceId(participantQuery.allianceQuery)

        return query {
            ParticipantTable.select { (ParticipantTable.teamNumber eq teamQuery.teamNumber) and (ParticipantTable.allianceId eq allianceId) }
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

    override suspend fun participantExists(participantQuery: ParticipantQuery): Boolean {
        return getParticipantRow(participantQuery)?.let { true } ?: false
    }

    suspend fun getMetricsByParticipant(participantData: ParticipantNode): Either<List<MetricNode>, DatabaseError> {
        val result = runCatching {
            query {
                ParticipantMetricTable.select { ParticipantMetricTable.participantId eq participantData.id }
            }
        }

        val participantMetricRows = result.getOrNull() ?: return Error(DatabaseUnknownError)

        val metricOrErrors = query {
            participantMetricRows.map { participantMetricRow ->
                val metricId = participantMetricRow[ParticipantMetricTable.metricId].value

                getMetricById(metricId)
            }
        }

        return if (metricOrErrors.all { it is Success }) {
            Success(metricOrErrors.map { (it as Success).value })
        } else {
            metricOrErrors.first { it is Error } as Error
        }
    }

    private suspend fun getMetricById(metricId: Int): Either<MetricNode, DatabaseError> {
        val result = runCatching {
            query {
                MetricTable.select { MetricTable.id eq metricId }.single()
            }
        }

        return result.getOrNull()?.let { Success(MetricNode.from(it)) } ?: Error(DatabaseUnknownError)
    }

    override suspend fun insertTeam(team: Team): Either<Unit, DatabaseError> {
        if (teamExists(team)) return Error(DatabaseThingExists("team"))

        val teamId = query {
            TeamTable.insertAndGetId {
                it[number] = team.number
                it[name] = team.name
                it[region] = team.region
            }.value
        }

        for (season in team.seasons) {
            insertSeason(season, teamId)
        }

        return Success(Unit)
    }

    private suspend fun insertSeason(season: Season, teamId: Int): Either<Unit, DatabaseError> {
        val seasonId = query {
            SeasonTable.insertAndGetId {
                it[this.teamId] = teamId
                it[year] = season.year
            }.value
        }

        for (event in season.events) {
            val eventId = getEventId(eventQueryOf(event))
            insertSeasonEvent(eventId, seasonId)
        }

        for (robot in season.robots) {
            insertRobot(robot, seasonId)
        }

        return Success(Unit)
    }

    override suspend fun insertSeason(season: Season, teamQuery: TeamQuery): Either<Unit, DatabaseError> {
        val seasonQuery = SeasonQuery(season.year, teamQuery)

        if (seasonExists(seasonQuery)) return Error(DatabaseThingExists("season"))

        val teamId = getTeamId(teamQuery)

        return insertSeason(season, teamId)
    }

    private suspend fun insertSeasonEvent(seasonId: Int, eventId: Int): Either<Unit, DatabaseError> {
        query {
            SeasonEventTable.insert {
                it[this.seasonId] = seasonId
                it[this.eventId] = eventId
            }
        }

        return Success(Unit)
    }

    override suspend fun seasonEventExists(eventQuery: EventQuery, seasonQuery: SeasonQuery): Boolean {
        val seasonId = getSeasonId(seasonQuery)
        val eventId = getEventId(eventQuery)

        val seasonEventRow = query {
            SeasonEventTable.select { (SeasonEventTable.seasonId eq seasonId) and (SeasonEventTable.eventId eq eventId) }
                .singleOrNull()
        }

        return seasonEventRow?.let { true } ?: false
    }

    override suspend fun insertSeasonEvent(
        eventQuery: EventQuery,
        seasonQuery: SeasonQuery
    ): Either<Unit, DatabaseError> {
        if (!eventExists(eventQuery)) return Error(DatabaseThingDoesNotExist("event"))
        if (!seasonExists(seasonQuery)) return Error(DatabaseThingDoesNotExist("season"))
        if (seasonEventExists(eventQuery, seasonQuery)) return Error(DatabaseThingExists("seasonEvent"))

        val seasonId = getSeasonId(seasonQuery)
        val eventId = getEventId(eventQuery)

        return insertSeasonEvent(seasonId, eventId)
    }

    private suspend fun insertRobot(robot: Robot, seasonId: Int): Either<Unit, DatabaseError> {
        query {
            RobotTable.insert {
                it[this.seasonId] = seasonId
                it[name] = robot.name
            }
        }

        return Success(Unit)
    }

    override suspend fun insertRobot(robot: Robot, seasonQuery: SeasonQuery): Either<Unit, DatabaseError> {
        val robotQuery = RobotQuery(robot.name, seasonQuery)

        if (robotExists(robotQuery)) return Error(DatabaseThingExists("robot"))

        val seasonId = getSeasonId(seasonQuery)

        return insertRobot(robot, seasonId)
    }

    override suspend fun insertEvent(event: Event): Either<Unit, DatabaseError> {
        if (eventExists(event)) return Error(DatabaseThingExists("event"))

        val eventId = query {
            EventTable.insertAndGetId {
                it[code] = event.code
                it[name] = event.name
                it[region] = event.region
                it[year] = event.year
                it[week] = event.week
            }.value
        }

        for (match in event.matches) {
            insertMatch(match, eventId)
        }

        return Success(Unit)
    }

    private suspend fun insertMatch(match: Match, eventId: Int): Either<Unit, DatabaseError> {
        val matchId = query {
            MatchTable.insertAndGetId {
                it[this.eventId] = eventId
                it[set] = match.set
                it[number] = match.number
                it[type] = match.type
            }.value
        }

        for (participant in match.alliances) {
            insertAlliance(participant, matchId)
        }

        return Success(Unit)
    }

    override suspend fun insertMatch(match: Match, eventQuery: EventQuery): Either<Unit, DatabaseError> {
        val matchQuery = MatchQuery(match.set, match.number, match.type, eventQuery)

        if (matchExists(matchQuery)) return Error(DatabaseThingExists("match"))

        val eventId = getEventId(eventQuery)

        return insertMatch(match, eventId)
    }

    private suspend fun insertParticipant(participant: Participant, allianceId: Int): Either<Unit, DatabaseError> {
        val participantId = query {
            ParticipantTable.insertAndGetId {
                it[this.allianceId] = allianceId
                it[teamNumber] = participant.teamNumber
            }.value
        }

        insertParticipantMetrics(participant.metrics, participantId)

        return Success(Unit)
    }

    override suspend fun insertParticipant(
        participant: Participant,
        allianceQuery: AllianceQuery
    ): Either<Unit, DatabaseError> {
        val teamQuery = TeamQuery(participant.teamNumber)
        val participantQuery = ParticipantQuery(teamQuery, allianceQuery)

        if (participantExists(participantQuery)) return Error(DatabaseThingExists("participant"))

        val allianceId = getAllianceId(allianceQuery)

        return insertParticipant(participant, allianceId)
    }

    private suspend fun insertAllianceMetrics(
        metrics: Map<String, String>,
        allianceId: Int
    ): Either<Unit, DatabaseError> {
        query {
            metrics.forEach { (key, value) ->
                val metricId = MetricTable.insertAndGetId {
                    it[MetricTable.key] = key
                    it[MetricTable.value] = value
                }

                AllianceMetricTable.insert {
                    it[AllianceMetricTable.allianceId] = allianceId
                    it[AllianceMetricTable.metricId] = metricId
                }
            }
        }

        return Success(Unit)
    }

    private suspend fun insertParticipantMetrics(
        metrics: Map<String, String>,
        participantId: Int
    ): Either<Unit, DatabaseError> {
        query {
            metrics.forEach { (key, value) ->
                val metricId = MetricTable.insertAndGetId {
                    it[MetricTable.key] = key
                    it[MetricTable.value] = value
                }

                ParticipantMetricTable.insert {
                    it[ParticipantMetricTable.participantId] = participantId
                    it[ParticipantMetricTable.metricId] = metricId
                }
            }
        }

        return Success(Unit)
    }

    override suspend fun insertParticipantMetrics(
        metrics: Map<String, String>,
        participantQuery: ParticipantQuery
    ): Either<Unit, DatabaseError> {
        val participantId = getParticipantId(participantQuery)

        return insertParticipantMetrics(metrics, participantId)
    }

    override suspend fun insertAllianceMetrics(
        metrics: Map<String, String>,
        allianceQuery: AllianceQuery
    ): Either<Unit, DatabaseError> {
        val allianceId = getAllianceId(allianceQuery)

        return insertAllianceMetrics(metrics, allianceId)
    }
}