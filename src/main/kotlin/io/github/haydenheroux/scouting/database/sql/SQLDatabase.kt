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

    private suspend fun getTeamNodes(): Result<List<TeamNode>> {
        return runCatching {
            query {
                TeamTable.selectAll().map { teamRow -> TeamNode.from(teamRow) }
            }
        }
    }


    override suspend fun getTeams(): Result<List<Team>> {
        val teamNodesResult = getTeamNodes()

        teamNodesResult.getOrNull()?.let { teamNodes ->
            return Result.success(teamNodes.map { teamNode -> teamNode.branch().tree().subtree() })
        } ?: run {
            return Result.failure(teamNodesResult.exceptionOrNull()!!)
        }
    }

    override suspend fun getTeamsSimple(): Result<List<Team>> {
        val teamNodesResult = getTeamNodes()

        teamNodesResult.getOrNull()?.let { teamNodes ->
            return Result.success(teamNodes.map { teamNode -> teamNode.branch().tree().leaf() })
        } ?: run {
            return Result.failure(teamNodesResult.exceptionOrNull()!!)
        }
    }

    private suspend fun getTeamNode(teamQuery: TeamQuery): Result<TeamNode> {
        return runCatching {
            val teamRow = getTeamRow(teamQuery)!!
            TeamNode.from(teamRow)
        }
    }

    override suspend fun getTeam(teamQuery: TeamQuery): Result<Team> {
        val teamNodeResult = getTeamNode(teamQuery)

        teamNodeResult.getOrNull()?.let { teamNode ->
            return Result.success(teamNode.branch().tree().subtree())
        } ?: run {
            return Result.failure(teamNodeResult.exceptionOrNull()!!)
        }
    }

    override suspend fun getTeamWithEvents(teamQuery: TeamQuery): Result<Team> {
        val teamNodeResult = getTeamNode(teamQuery)

        teamNodeResult.getOrNull()?.let { teamNode ->
            return Result.success(teamNode.branch().tree().subtree(2))
        } ?: run {
            return Result.failure(teamNodeResult.exceptionOrNull()!!)
        }
    }

    override suspend fun getTeamWithMatches(teamQuery: TeamQuery): Result<Team> {
        val teamNodeResult = getTeamNode(teamQuery)

        teamNodeResult.getOrNull()?.let { teamNode ->
            return Result.success(teamNode.branch().tree().subtree(4))
        } ?: run {
            return Result.failure(teamNodeResult.exceptionOrNull()!!)
        }
    }

    suspend fun getTeamBySeason(seasonData: SeasonNode): Result<TeamNode> {
        return query {
            val seasonRow = SeasonTable.select { SeasonTable.id eq seasonData.id }.single()
            val teamId = seasonRow[SeasonTable.teamId].value

            getTeamById(teamId)
        }
    }

    suspend fun getTeamByParticipant(participantData: ParticipantNode): Result<TeamNode> {
        return query {
            val participantRow = ParticipantTable.select { ParticipantTable.id eq participantData.id }.single()
            val teamId = participantRow[ParticipantTable.teamId].value

            getTeamById(teamId)
        }
    }

    private suspend fun getTeamById(teamId: Int): Result<TeamNode> {
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

    override suspend fun teamExists(teamQuery: TeamQuery): Boolean {
        return getTeamRow(teamQuery)?.let { true } ?: false
    }

    override suspend fun teamExists(team: Team): Boolean {
        return teamExists(teamQueryOf(team))
    }

    private suspend fun getSeasonNode(seasonQuery: SeasonQuery): Result<SeasonNode> {
        return runCatching {
            val seasonRow = getSeasonRow(seasonQuery)!!
            SeasonNode.from(seasonRow)
        }
    }

    override suspend fun getSeason(seasonQuery: SeasonQuery): Result<Season> {
        val seasonNodeResult = getSeasonNode(seasonQuery)

        seasonNodeResult.getOrNull()?.let { seasonNode ->
            return Result.success(seasonNode.branch().tree().subtree())
        } ?: run {
            return Result.failure(seasonNodeResult.exceptionOrNull()!!)
        }
    }

    override suspend fun getSeasonWithEventsAndTeam(seasonQuery: SeasonQuery): Result<Pair<Season, Team>> {
        val seasonNodeResult = getSeasonNode(seasonQuery)

        seasonNodeResult.getOrNull()?.let { seasonNode ->
            val season = seasonNode.branch().tree().subtree(1)
            val team = seasonNode.parent().team.tree().leaf()
            return Result.success(Pair(season, team))
        } ?: run {
            return Result.failure(seasonNodeResult.exceptionOrNull()!!)
        }
    }

    override suspend fun getSeasonWithMatchesAndTeam(seasonQuery: SeasonQuery): Result<Pair<Season, Team>> {
        val seasonNodeResult = getSeasonNode(seasonQuery)

        seasonNodeResult.getOrNull()?.let { seasonNode ->
            val season = seasonNode.branch().tree().subtree(4)
            val team = seasonNode.parent().team.tree().leaf()
            return Result.success(Pair(season, team))
        } ?: run {
            return Result.failure(seasonNodeResult.exceptionOrNull()!!)
        }
    }

    suspend fun getSeasonByRobot(robotData: RobotNode): Result<SeasonNode> {
        return query {
            val robotRow = RobotTable.select { RobotTable.id eq robotData.id }.single()
            val seasonId = robotRow[RobotTable.seasonId].value

            getSeasonById(seasonId)
        }
    }

    private suspend fun getSeasonById(seasonId: Int): Result<SeasonNode> {
        return runCatching {
            val seasonRow = getSeasonRow(seasonId)!!
            SeasonNode.from(seasonRow)
        }
    }

    suspend fun getSeasonsByTeam(teamData: TeamNode): Result<List<SeasonNode>> {
        return runCatching {
            query {
                SeasonTable.select { SeasonTable.teamId eq teamData.id }.map { seasonRow -> SeasonNode.from(seasonRow) }
            }
        }
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

    suspend fun getRobotsBySeason(seasonData: SeasonNode): Result<List<RobotNode>> {
        return runCatching {
            query {
                RobotTable.select { RobotTable.seasonId eq seasonData.id }
                    .map { robotRow -> RobotNode.from(robotRow) }
            }
        }
    }

    override suspend fun getRobot(robotQuery: RobotQuery): Result<Robot> {
        return runCatching {
            val robotRow = getRobotRow(robotQuery)!!
            RobotNode.from(robotRow).branch().tree().subtree()
        }
    }

    suspend fun getRobotById(robotId: Int): Result<RobotNode> {
        return runCatching {
            val robotRow = getRobotRow(robotId)!!
            RobotNode.from(robotRow)
        }
    }

    private suspend fun getRobotRow(robotQuery: RobotQuery): ResultRow? {
        val seasonId = getSeasonId(robotQuery.seasonQuery)

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

    override suspend fun robotExists(robotQuery: RobotQuery): Boolean {
        return getRobotRow(robotQuery)?.let { true } ?: false
    }

    private suspend fun getEventNodes(): Result<List<EventNode>> {
        return runCatching {
            query {
                EventTable.selectAll().map { eventRow -> EventNode.from(eventRow) }
            }
        }
    }

    override suspend fun getEvents(): Result<List<Event>> {
        val eventNodesResult = getEventNodes()

        eventNodesResult.getOrNull()?.let { eventNodes ->
            return Result.success(eventNodes.map { eventNode -> eventNode.branch().tree().subtree() })
        } ?: run {
            return Result.failure(eventNodesResult.exceptionOrNull()!!)
        }
    }

    override suspend fun getEventsSimple(): Result<List<Event>> {
        val eventNodesResult = getEventNodes()

        eventNodesResult.getOrNull()?.let { eventNodes ->
            return Result.success(eventNodes.map { eventNode -> eventNode.tree().leaf() })
        } ?: run {
            return Result.failure(eventNodesResult.exceptionOrNull()!!)
        }
    }

    private suspend fun getEventNode(eventQuery: EventQuery): Result<EventNode> {
        return runCatching {
            val eventRow = getEventRow(eventQuery)!!
            EventNode.from(eventRow)
        }
    }

    override suspend fun getEvent(eventQuery: EventQuery): Result<Event> {
        val eventNodeResult = getEventNode(eventQuery)

        eventNodeResult.getOrNull()?.let { eventNode ->
            return Result.success(eventNode.branch().tree().subtree())
        } ?: run {
            return Result.failure(eventNodeResult.exceptionOrNull()!!)
        }
    }

    override suspend fun getEventWithMatches(eventQuery: EventQuery): Result<Event> {
        val eventNodeResult = getEventNode(eventQuery)

        eventNodeResult.getOrNull()?.let { eventNode ->
            return Result.success(eventNode.branch().tree().subtree(1))
        } ?: run {
            return Result.failure(eventNodeResult.exceptionOrNull()!!)
        }
    }

    override suspend fun getEventWithParticipants(eventQuery: EventQuery): Result<Event> {
        val eventNodeResult = getEventNode(eventQuery)

        eventNodeResult.getOrNull()?.let { eventNode ->
            return Result.success(eventNode.branch().tree().subtree(3))
        } ?: run {
            return Result.failure(eventNodeResult.exceptionOrNull()!!)
        }
    }

    suspend fun getEventsBySeason(seasonData: SeasonNode): Result<List<EventNode>> {
        return runCatching {
            query {
                SeasonEventTable.select { SeasonEventTable.seasonId eq seasonData.id }.map { seasonEventRow ->
                    val eventId = seasonEventRow[SeasonEventTable.eventId].value

                    getEventById(eventId).getOrThrow()
                }
            }
        }
    }

    suspend fun getEventByMatch(matchData: MatchNode): Result<EventNode> {
        return query {
            val matchRow = MatchTable.select { MatchTable.id eq matchData.id }.single()
            val eventId = matchRow[MatchTable.eventId].value

            getEventById(eventId)
        }
    }

    private suspend fun getEventById(eventId: Int): Result<EventNode> {
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

    override suspend fun eventExists(eventQuery: EventQuery): Boolean {
        return getEventRow(eventQuery)?.let { true } ?: false
    }

    override suspend fun eventExists(event: Event): Boolean {
        return eventExists(eventQueryOf(event))
    }

    private suspend fun getMatchNode(matchQuery: MatchQuery): Result<MatchNode> {
        return runCatching {
            val matchRow = getMatchRow(matchQuery)!!
            MatchNode.from(matchRow)
        }
    }

    override suspend fun getMatch(matchQuery: MatchQuery): Result<Match> {
        val matchNodeResult = getMatchNode(matchQuery)

        matchNodeResult.getOrNull()?.let { matchNode ->
            return Result.success(matchNode.branch().tree().subtree())
        } ?: run {
            return Result.failure(matchNodeResult.exceptionOrNull()!!)
        }
    }

    override suspend fun getMatchWithMetricsAndEvent(matchQuery: MatchQuery): Result<Pair<Match, Event>> {
        val matchNodeResult = getMatchNode(matchQuery)

        matchNodeResult.getOrNull()?.let { matchNode ->
            val match = matchNode.branch().tree().subtree(2)
            val event = matchNode.parent().event.tree().leaf()

            return Result.success(Pair(match, event))
        } ?: run {
            return Result.failure(matchNodeResult.exceptionOrNull()!!)
        }
    }

    suspend fun getMatchByParticipant(participantData: ParticipantNode): Result<MatchNode> {
        return query {
            val participantRow =
                ParticipantTable.select { ParticipantTable.id eq participantData.id }.single()
            val matchId = participantRow[ParticipantTable.matchId].value

            getMatchById(matchId)
        }
    }

    private suspend fun getMatchById(matchId: Int): Result<MatchNode> {
        return runCatching {
            val matchRow = getMatchRow(matchId)!!
            MatchNode.from(matchRow)
        }
    }

    suspend fun getMatchesByEvent(eventData: EventNode): Result<List<MatchNode>> {
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

    override suspend fun matchExists(matchQuery: MatchQuery): Boolean {
        return getMatchRow(matchQuery)?.let { true } ?: false
    }

    override suspend fun getParticipant(participantQuery: ParticipantQuery): Result<Participant> {
        return runCatching {
            val participantRow = getParticipantRow(participantQuery)!!
            ParticipantNode.from(participantRow).branch().tree().subtree()
        }
    }

    suspend fun getParticipantByMetric(metricData: MetricNode): Result<ParticipantNode> {
        return query {
            val metricRow = MetricTable.select { MetricTable.id eq metricData.id }.single()
            val participantId = metricRow[MetricTable.participantId].value

            getParticipantById(participantId)
        }
    }

    private suspend fun getParticipantById(participantId: Int): Result<ParticipantNode> {
        return runCatching {
            val participantRow = getParticipantRow(participantId)!!
            ParticipantNode.from(participantRow)
        }
    }

    suspend fun getParticipantsByMatch(matchData: MatchNode): Result<List<ParticipantNode>> {
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

    override suspend fun participantExists(participantQuery: ParticipantQuery): Boolean {
        return getParticipantRow(participantQuery)?.let { true } ?: false
    }

    suspend fun getMetricsByParticipant(participantData: ParticipantNode): Result<List<MetricNode>> {
        return runCatching {
            query {
                MetricTable.select { MetricTable.participantId eq participantData.id }
                    .map { metricRow -> MetricNode.from(metricRow) }
            }
        }
    }

    private suspend fun getMetricId(metricQuery: MetricQuery): Int {
        return getMetricRow(metricQuery)!![ParticipantTable.id].value
    }

    override suspend fun metricExists(metricQuery: MetricQuery): Boolean {
        return getMetricRow(metricQuery)?.let { true } ?: false
    }

    private suspend fun getMetricRow(metricQuery: MetricQuery): ResultRow? {
        val participantId = getParticipantId(metricQuery.participantQuery)

        return query {
            MetricTable.select { (MetricTable.key eq metricQuery.key) and (MetricTable.participantId eq participantId) }
                .singleOrNull()
        }
    }

    override suspend fun insertTeam(team: Team): Result<Unit> {
        if (teamExists(team)) return Result.failure(Exception("Team exists"))

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

        return Result.success(Unit)
    }

    private suspend fun insertSeason(season: Season, teamId: Int): Result<Unit> {
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

        return Result.success(Unit)
    }

    override suspend fun insertSeason(season: Season, teamQuery: TeamQuery): Result<Unit> {
        val seasonQuery = SeasonQuery(season.year, teamQuery)

        if (seasonExists(seasonQuery)) return Result.failure(Exception("Season exists"))

        val teamId = getTeamId(teamQuery)

        return insertSeason(season, teamId)
    }

    override suspend fun seasonExists(season: Season, team: Team): Boolean {
        return seasonExists(seasonQueryOf(season, team))
    }

    private suspend fun insertSeasonEvent(seasonId: Int, eventId: Int): Result<Unit> {
        query {
            SeasonEventTable.insert {
                it[this.seasonId] = seasonId
                it[this.eventId] = eventId
            }
        }

        return Result.success(Unit)
    }

    override suspend fun insertSeasonEvent(eventQuery: EventQuery, seasonQuery: SeasonQuery): Result<Unit> {
        if (!eventExists(eventQuery)) return Result.failure(Exception("Event does not exist"))
        if (!seasonExists(seasonQuery)) return Result.failure(Exception("Season does not exist"))

        val seasonId = getSeasonId(seasonQuery)
        val eventId = getEventId(eventQuery)

        return insertSeasonEvent(seasonId, eventId)
    }

    private suspend fun insertRobot(robot: Robot, seasonId: Int): Result<Unit> {
        query {
            RobotTable.insert {
                it[this.seasonId] = seasonId
                it[name] = robot.name
            }
        }

        return Result.success(Unit)
    }

    override suspend fun insertRobot(robot: Robot, seasonQuery: SeasonQuery): Result<Unit> {
        val robotQuery = RobotQuery(robot.name, seasonQuery)

        if (robotExists(robotQuery)) return Result.failure(Exception("Robot exists"))

        val seasonId = getSeasonId(seasonQuery)

        return insertRobot(robot, seasonId)
    }

    override suspend fun robotExists(robot: Robot, season: Season, team: Team): Boolean {
        return robotExists(robotQueryOf(robot, season, team))
    }

    override suspend fun insertEvent(event: Event): Result<Unit> {
        if (eventExists(event)) return Result.failure(Exception("Event exists"))

        val eventId = query {
            EventTable.insertAndGetId {
                it[name] = event.name
                it[region] = event.region
                it[year] = event.year
                it[week] = event.week
            }.value
        }

        for (match in event.matches) {
            insertMatch(match, eventId)
        }

        return Result.success(Unit)
    }

    private suspend fun insertMatch(match: Match, eventId: Int): Result<Unit> {
        val matchId = query {
            MatchTable.insertAndGetId {
                it[this.eventId] = eventId
                it[set] = match.set
                it[number] = match.number
                it[type] = match.type
            }.value
        }

        for (participant in match.participants) {
            if (participant.team == null) {
                println("TODO: Unable to insert participant because team was null")
            } else {
                val teamId = getTeamId(teamQueryOf(participant.team))
                insertParticipant(participant, teamId, matchId)
            }
        }

        return Result.success(Unit)
    }

    override suspend fun insertMatch(match: Match, eventQuery: EventQuery): Result<Unit> {
        val matchQuery = MatchQuery(match.set, match.number, match.type, eventQuery)

        if (matchExists(matchQuery)) return Result.failure(Exception("Match exists"))

        val eventId = getEventId(eventQuery)

        return insertMatch(match, eventId)
    }

    override suspend fun matchExists(match: Match, event: Event): Boolean {
        return matchExists(matchQueryOf(match, event))
    }

    private suspend fun insertParticipant(participant: Participant, teamId: Int, matchId: Int): Result<Unit> {
        val participantId = query {
            ParticipantTable.insertAndGetId {
                it[this.matchId] = matchId
                it[this.teamId] = teamId
                it[alliance] = participant.alliance
            }.value
        }

        for (metric in participant.metrics) {
            insertMetric(metric, participantId)
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

        return insertParticipant(participant, teamId, matchId)
    }

    override suspend fun participantExists(participant: Participant, match: Match, event: Event): Boolean {
        return participantExists(
            ParticipantQuery(
                teamQueryOf(participant.team!!),
                matchQueryOf(match, event)
            )
        )
    }

    private suspend fun insertMetric(metric: Metric, participantId: Int): Result<Unit> {
        query {
            MetricTable.insert {
                it[MetricTable.participantId] = participantId
                it[key] = metric.key
                it[value] = metric.value
            }
        }

        return Result.success(Unit)
    }

    override suspend fun insertMetric(metric: Metric, participantQuery: ParticipantQuery): Result<Unit> {
        if (metricExists(MetricQuery(metric.key, participantQuery))) return Result.failure(Exception("Metric exists"))

        val participantId = getParticipantId(participantQuery)

        return insertMetric(metric, participantId)
    }

    override suspend fun metricExists(metric: Metric, participant: Participant, match: Match, event: Event): Boolean {
        return metricExists(metricQueryOf(metric, participant.team!!, match, event))
    }
}