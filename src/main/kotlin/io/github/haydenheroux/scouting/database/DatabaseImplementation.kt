package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.models.enums.Region
import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.event.Events
import io.github.haydenheroux.scouting.models.event.SeasonEvents
import io.github.haydenheroux.scouting.models.match.*
import io.github.haydenheroux.scouting.models.team.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseImplementation : DatabaseInterface {

    override suspend fun getTeams(): List<Team> {
        return query {
            Teams.selectAll().map { rowToTeam(it) }
        }
    }

    override suspend fun getTeamByNumber(number: Int): Team {
        val row: ResultRow = getTeamRowByNumber(number)!!
        return rowToTeam(row)
    }

    private suspend fun getTeamRow(team: Team): ResultRow? {
        return getTeamRowByNumber(team.number)
    }

    private suspend fun getTeamRow(teamId: Int): ResultRow? {
        return query {
            Teams.select { Teams.id eq teamId }.singleOrNull()
        }
    }

    private suspend fun getTeamRowByNumber(number: Int): ResultRow? {
        return query {
            Teams.select { Teams.number eq number }.singleOrNull()
        }
    }

    private suspend fun getTeamId(team: Team): Int {
        val row: ResultRow = getTeamRow(team)!!
        return row[Teams.id].value
    }

    private suspend fun teamExists(team: Team): Boolean {
        val row = getTeamRow(team)
        return row?.let { true } ?: false
    }

    private suspend fun rowToTeam(row: ResultRow): Team {
        val teamId = row[Teams.id].value

        val number = row[Teams.number]
        val name = row[Teams.name]
        val region = row[Teams.region]
        val seasons = getSeasonsForTeam(teamId)

        return Team(number, name, region, seasons)
    }

    private suspend fun getSeasonsForTeam(teamId: Int): List<Season> {
        return query {
            Seasons.select { Seasons.team eq teamId }.map { rowToSeason(it) }
        }
    }

    private suspend fun getSeasonRow(season: Season, parentTeam: Team): ResultRow? {
        val teamId = getTeamId(parentTeam)

        return query {
            Seasons.select { (Seasons.year eq season.year) and (Seasons.team eq teamId) }.singleOrNull()
        }
    }

    private suspend fun getSeasonRow(seasonId: Int): ResultRow? {
        return query {
            Seasons.select { Seasons.id eq seasonId }.singleOrNull()
        }
    }

    private suspend fun getSeasonId(season: Season, parentTeam: Team): Int {
        val row = getSeasonRow(season, parentTeam)!!
        return row[Seasons.id].value
    }

    private suspend fun seasonExists(season: Season, parentTeam: Team): Boolean {
        val row = getSeasonRow(season, parentTeam)
        return row?.let { true } ?: false
    }

    private suspend fun rowToSeason(row: ResultRow): Season {
        val seasonId: Int = row[Seasons.id].value

        val year = row[Seasons.year]
        val robots = getRobotsForSeason(seasonId)
        val events = getEventsForSeason(seasonId)

        return Season(year, robots, events)
    }

    private suspend fun getRobotsForSeason(seasonId: Int): List<Robot> {
        return query {
            Robots.select { Robots.season eq seasonId }.map { rowToRobot(it) }
        }
    }

    private suspend fun getRobotRow(robot: Robot, parentSeason: Season, parentTeam: Team): ResultRow? {
        val seasonId = getSeasonId(parentSeason, parentTeam)

        return query {
            Robots.select { (Robots.name eq robot.name) and (Robots.season eq seasonId) }.singleOrNull()
        }
    }

    private suspend fun getRobotRow(robotId: Int): ResultRow? {
        return query {
            Robots.select { Robots.id eq robotId }.singleOrNull()
        }
    }

    private suspend fun getRobotId(robot: Robot, parentSeason: Season, parentTeam: Team): Int {
        val row = getRobotRow(robot, parentSeason, parentTeam)!!
        return row[Robots.id].value
    }

    private suspend fun robotExists(robot: Robot, parentSeason: Season, parentTeam: Team): Boolean {
        val row = getRobotRow(robot, parentSeason, parentTeam)
        return row?.let { true } ?: false
    }

    private fun rowToRobot(row: ResultRow): Robot {
        val name = row[Robots.name]

        return Robot(name)
    }

    override suspend fun getEvents(): List<Event> {
        return query {
            Events.selectAll().map { rowToEvent(it) }
        }
    }

    override suspend fun getEventsByRegion(region: Region): List<Event> {
        return query {
            Events.select { Events.region eq region }.map { rowToEvent(it) }
        }
    }

    private suspend fun getEventRow(event: Event): ResultRow? {
        return query {
            Events.select { (Events.name eq event.name) and (Events.year eq event.year) and (Events.week eq event.week) }
                .singleOrNull()
        }
    }

    private suspend fun getEventRow(eventId: Int): ResultRow? {
        return query {
            Events.select { Events.id eq eventId }.singleOrNull()
        }
    }

    private suspend fun getEventId(event: Event): Int {
        val row = getEventRow(event)!!
        return row[Events.id].value
    }

    private suspend fun eventExists(event: Event): Boolean {
        val row = getEventRow(event)
        return row?.let { true } ?: false
    }

    private suspend fun rowToEvent(row: ResultRow): Event {
        val eventId: Int = row[Events.id].value

        val name = row[Events.name]
        val region = row[Events.region]
        val year = row[Events.year]
        val week = row[Events.week]
        val matches = getMatchesForEvent(eventId)

        return Event(name, region, year, week, matches)
    }

    private suspend fun getMatchesForEvent(eventId: Int): List<Match> {
        return query {
            Matches.select { Matches.event eq eventId }.map { rowToMatch(it) }
        }
    }

    private suspend fun getEventsForSeason(seasonId: Int): List<Event> {
        return query {
            SeasonEvents.select { SeasonEvents.season eq seasonId }
                .map { rowToEvent(getEventRow(it[SeasonEvents.event].value)!!) }
        }
    }

    private suspend fun getMatchRow(match: Match, parentEvent: Event): ResultRow? {
        val eventId = getEventId(parentEvent)

        return query {
            Matches.select { (Matches.event eq eventId) and (Matches.number eq match.number) and (Matches.type eq match.type) }
                .singleOrNull()
        }
    }

    private suspend fun getMatchRow(matchId: Int): ResultRow? {
        return query {
            Matches.select { Matches.id eq matchId }.singleOrNull()
        }
    }

    private suspend fun getMatchId(match: Match, parentEvent: Event): Int {
        val row = getMatchRow(match, parentEvent)!!
        return row[Matches.id].value
    }

    private suspend fun matchExists(match: Match, parentEvent: Event): Boolean {
        val row = getMatchRow(match, parentEvent)
        return row?.let { true } ?: false
    }

    private suspend fun rowToMatch(row: ResultRow): Match {
        val matchId: Int = row[Matches.id].value

        val number = row[Matches.number]
        val type = row[Matches.type]
        val metrics = getMetricsForMatch(matchId)

        return Match(number, type, metrics)
    }

    private suspend fun getMetricsForMatch(matchId: Int): List<Metric> {
        return query {
            Metrics.select { Metrics.match eq matchId }.map { rowToMetric(it) }
        }
    }

    private suspend fun getMetricRow(
        metric: Metric,
        parentMatch: Match,
        parentEvent: Event,
        parentSeason: Season,
        parentTeam: Team
    ): ResultRow? {
        val matchId = getMatchId(parentMatch, parentEvent)
        val robotId = getRobotId(metric.robot, parentSeason, parentTeam)

        return query {
            Metrics.select { (Metrics.match eq matchId) and (Metrics.robot eq robotId) }.singleOrNull()
        }
    }

    private suspend fun getMetricRow(metricId: Int): ResultRow? {
        return query {
            Metrics.select { Metrics.id eq metricId }.singleOrNull()
        }
    }

    private suspend fun getMetricId(
        metric: Metric,
        parentMatch: Match,
        parentEvent: Event,
        parentSeason: Season,
        parentTeam: Team
    ): Int {
        val row = getMetricRow(metric, parentMatch, parentEvent, parentSeason, parentTeam)!!
        return row[Metrics.id].value
    }

    private suspend fun metricExists(
        metric: Metric,
        parentMatch: Match,
        parentEvent: Event,
        parentSeason: Season,
        parentTeam: Team
    ): Boolean {
        val row = getMetricRow(metric, parentMatch, parentEvent, parentSeason, parentTeam)
        return row?.let { true } ?: false
    }

    private suspend fun rowToMetric(row: ResultRow): Metric {
        val metricId = row[Metrics.id].value
        val robotId = row[Metrics.robot].value

        val robot = rowToRobot(getRobotRow(robotId)!!)
        val alliance = row[Metrics.alliance]
        val gameMetrics = getGameMetricsForMetric(metricId)

        return Metric(robot, alliance, gameMetrics)
    }

    private suspend fun getGameMetricsForMetric(metricId: Int): List<GameMetric> {
        return query {
            GameMetrics.select { GameMetrics.metric eq metricId }.map { rowToGameMetric(it) }
        }
    }

    private suspend fun getGameMetricRow(
        gameMetric: GameMetric,
        parentMetric: Metric,
        parentMatch: Match,
        parentEvent: Event,
        parentSeason: Season,
        parentTeam: Team
    ): ResultRow? {
        val metricId = getMetricId(parentMetric, parentMatch, parentEvent, parentSeason, parentTeam)

        return query {
            GameMetrics.select { GameMetrics.metric eq metricId }.singleOrNull()
        }
    }

    private suspend fun getGameMetricRow(gameMetricId: Int): ResultRow? {
        return query {
            GameMetrics.select { GameMetrics.id eq gameMetricId }.singleOrNull()
        }
    }

    private suspend fun getGameMetricId(
        gameMetric: GameMetric,
        parentMetric: Metric,
        parentMatch: Match,
        parentEvent: Event,
        parentSeason: Season,
        parentTeam: Team
    ): Int {
        val row = getGameMetricRow(gameMetric, parentMetric, parentMatch, parentEvent, parentSeason, parentTeam)!!
        return row[GameMetrics.id].value
    }

    private suspend fun gameMetricExists(
        gameMetric: GameMetric,
        parentMetric: Metric,
        parentMatch: Match,
        parentEvent: Event,
        parentSeason: Season,
        parentTeam: Team
    ): Boolean {
        val row = getGameMetricRow(gameMetric, parentMetric, parentMatch, parentEvent, parentSeason, parentTeam)
        return row?.let { true } ?: false
    }

    private fun rowToGameMetric(row: ResultRow): GameMetric {
        val key = row[GameMetrics.key]
        val value = row[GameMetrics.value]

        return GameMetric(key, value)
    }

    override suspend fun insertTeam(team: Team) {
        if (teamExists(team)) throw Exception("Team exists.")

        transaction {
            Teams.insert {
                it[number] = team.number
                it[name] = team.name
                it[region] = team.region
            }
        }

        for (season in team.seasons) {
            insertSeason(season, team)
        }
    }

    override suspend fun insertSeason(season: Season, team: Team) {
        if (seasonExists(season, team)) throw Exception("Season exists.")

        val teamId = getTeamId(team)

        transaction {
            Seasons.insert {
                it[Seasons.team] = teamId
                it[year] = season.year
            }
        }

        for (robot in season.robots) {
            insertRobot(robot, season, team)
        }

        for (event in season.events) {
            insertSeasonEvent(event, season, team)
        }
    }

    override suspend fun insertRobot(robot: Robot, season: Season, team: Team) {
        if (robotExists(robot, season, team)) throw Exception("Robot exists.")

        val seasonId = findSeasonId(season, team)

        transaction {
            Robots.insert {
                it[Robots.season] = seasonId
                it[name] = robot.name
            }
        }
    }

    override suspend fun insertEvent(event: Event) {
        if (eventExists(event)) throw Exception("Event exists.")

        transaction {
            Events.insert {
                it[name] = event.name
                it[region] = event.region
                it[year] = event.year
                it[week] = event.week
            }
        }

        for (match in event.matches) {
            TODO()
        }
    }

    override suspend fun insertSeasonEvent(event: Event, season: Season, team: Team) {
        if (!eventExists(event)) insertEvent(event)
        if (seasonEventExists(event, season, team)) throw Exception("Season event exists.")

        val eventId = findEventId(event)
        val seasonId = findSeasonId(season, team)

        transaction {
            SeasonEvents.insert {
                it[SeasonEvents.event] = eventId
                it[SeasonEvents.season] = seasonId
            }
        }
    }

    private suspend fun seasonEventExists(event: Event, season: Season, team: Team): Boolean {
        val eventId = findEventId(event)
        val seasonId = findSeasonId(season, team)

        return query {
            !SeasonEvents.select { (SeasonEvents.event eq eventId) and (SeasonEvents.season eq seasonId) }.empty()
        }
    }

    override suspend fun insertMatch(match: Match, event: Event, season: Season, team: Team) {
        if (matchExists(match, event)) throw Exception("Match exists.")

        transaction {
            Matches.insert {
                it[number] = match.number
                it[type] = match.type
            }
        }

        for (metric in match.metrics) {
            insertMetric(metric, match, event, season, team)
        }
    }

    override suspend fun insertMetric(metric: Metric, match: Match, event: Event, season: Season, team: Team) {
        if (metricExists(metric, match, event, season, team)) throw Exception("Metric exists.")

        TODO()
    }

    override suspend fun insertGameMetric(gameMetric: GameMetric, metric: Metric) {
        val metricId = findMetricId(metric)

        GameMetrics.insert {
            it[GameMetrics.metric] = metricId
            it[key] = gameMetric.key
            it[value] = gameMetric.value
        }
    }

    private suspend fun findMetricId(metric: Metric): Int {
        TODO("")
    }

    private suspend fun findRobotId(robot: Robot, season: Season, team: Team): Int {
        val seasonId = findSeasonId(season, team)

        return query {
            Robots.select { (Robots.name eq robot.name) and (Robots.season eq seasonId) }.map { it[Robots.id].value }[0]
        }
    }

    private suspend fun findMatchId(match: Match, event: Event): Int {
        val eventId = findEventId(event)

        return query {
            Matches.select { (Matches.event eq eventId) and (Matches.number eq match.number) and (Matches.type eq match.type) }
                .map { it[Matches.id].value }[0]
        }
    }

    private suspend fun findEventId(event: Event): Int {
        return query {
            Events.select { (Events.name eq event.name) and (Events.year eq event.year) and (Events.week eq event.week) }
                .map { it[Events.id].value }[0]
        }
    }

    private suspend fun findSeasonId(season: Season, team: Team): Int {
        val teamId = getTeamId(team)

        return query {
            Seasons.select { (Seasons.year eq season.year) and (Seasons.team eq teamId) }
                .map { it[Seasons.id].value }[0]
        }
    }
}

val db = DatabaseImplementation()
