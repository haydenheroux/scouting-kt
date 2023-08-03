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

    override suspend fun getSeasonByNumberYear(number: Int, year: Int): Season {
        val team = getTeamByNumber(number)

        val season = team.seasons.single { it.year == year }
        season.team = team

        return season
    }

    private suspend fun getSeasonRow(season: Season): ResultRow? {
        val teamId = getTeamId(season.team!!)

        return query {
            Seasons.select { (Seasons.year eq season.year) and (Seasons.team eq teamId) }.singleOrNull()
        }
    }

    private suspend fun getSeasonRow(seasonId: Int): ResultRow? {
        return query {
            Seasons.select { Seasons.id eq seasonId }.singleOrNull()
        }
    }

    private suspend fun getSeasonId(season: Season): Int {
        val row = getSeasonRow(season)!!
        return row[Seasons.id].value
    }

    private suspend fun seasonExists(season: Season): Boolean {
        val row = getSeasonRow(season)
        return row?.let { true } ?: false
    }

    private suspend fun rowToSeason(row: ResultRow): Season {
        val seasonId: Int = row[Seasons.id].value

        val team = null // rowToTeam(getTeamRow(row[Seasons.team].value)!!)
        val year = row[Seasons.year]
        val robots = getRobotsForSeason(seasonId)
        val events = getEventsForSeason(seasonId)

        return Season(team, year, robots, events)
    }

    private suspend fun getRobotsForSeason(seasonId: Int): List<Robot> {
        return query {
            Robots.select { Robots.season eq seasonId }.map { rowToRobot(it) }
        }
    }

    private suspend fun getRobotRow(robot: Robot): ResultRow? {
        val seasonId = getSeasonId(robot.season!!)

        return query {
            Robots.select { (Robots.name eq robot.name) and (Robots.season eq seasonId) }.singleOrNull()
        }
    }

    private suspend fun getRobotRow(robotId: Int): ResultRow? {
        return query {
            Robots.select { Robots.id eq robotId }.singleOrNull()
        }
    }

    private suspend fun getRobotId(robot: Robot): Int {
        val row = getRobotRow(robot)!!
        return row[Robots.id].value
    }

    private suspend fun robotExists(robot: Robot): Boolean {
        val row = getRobotRow(robot)
        return row?.let { true } ?: false
    }

    private suspend fun rowToRobot(row: ResultRow): Robot {
        val seasonId = row[Robots.season].value

        val season = null // rowToSeason(getSeasonRow(seasonId)!!)
        val name = row[Robots.name]

        return Robot(season, name)
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

    override suspend fun getEventByNameRegionYearWeek(name: String, region: Region, year: Int, week: Int): Event {
        val row = getEventRowByNameRegionYearWeek(name, region, year, week)!!
        return rowToEvent(row)
    }

    private suspend fun getEventRow(event: Event): ResultRow? {
        return query {
            Events.select { (Events.name eq event.name) and (Events.region eq event.region) and (Events.year eq event.year) and (Events.week eq event.week) }
                .singleOrNull()
        }
    }

    private suspend fun getEventRow(eventId: Int): ResultRow? {
        return query {
            Events.select { Events.id eq eventId }.singleOrNull()
        }
    }

    private suspend fun getEventRowByNameRegionYearWeek(
        name: String,
        region: Region,
        year: Int,
        week: Int
    ): ResultRow? {
        return query {
            Events.select { (Events.name eq name) and (Events.region eq region) and (Events.year eq year) and (Events.week eq week) }
                .singleOrNull()
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

    private suspend fun getMatchRow(match: Match): ResultRow? {
        val eventId = getEventId(match.event!!)

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

    private suspend fun getMatchId(match: Match): Int {
        val row = getMatchRow(match)!!
        return row[Matches.id].value
    }

    private suspend fun matchExists(match: Match): Boolean {
        val row = getMatchRow(match)
        return row?.let { true } ?: false
    }

    private suspend fun rowToMatch(row: ResultRow): Match {
        val eventId = row[Matches.event].value
        val matchId = row[Matches.id].value

        val event = null // rowToEvent(getEventRow(eventId)!!)
        val number = row[Matches.number]
        val type = row[Matches.type]
        val metrics = getMetricsForMatch(matchId)

        return Match(event, number, type, metrics)
    }

    private suspend fun getMetricsForMatch(matchId: Int): List<Metric> {
        return query {
            Metrics.select { Metrics.match eq matchId }.map { rowToMetric(it) }
        }
    }

    private suspend fun getMetricRow(
        metric: Metric,
    ): ResultRow? {
        val matchId = getMatchId(metric.match!!)
        val robotId = getRobotId(metric.robot!!)

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
    ): Int {
        val row = getMetricRow(metric)!!
        return row[Metrics.id].value
    }

    private suspend fun metricExists(
        metric: Metric,
    ): Boolean {
        val row = getMetricRow(metric)
        return row?.let { true } ?: false
    }

    private suspend fun rowToMetric(row: ResultRow): Metric {
        val metricId = row[Metrics.id].value
        val matchId = row[Metrics.match].value
        val robotId = row[Metrics.robot].value

        val match = null // rowToMatch(getMatchRow(matchId)!!)
        val robot = null // rowToRobot(getRobotRow(robotId)!!)
        val alliance = row[Metrics.alliance]
        val gameMetrics = getGameMetricsForMetric(metricId)

        return Metric(match, robot, alliance, gameMetrics)
    }

    private suspend fun getGameMetricsForMetric(metricId: Int): List<GameMetric> {
        return query {
            GameMetrics.select { GameMetrics.metric eq metricId }.map { rowToGameMetric(it) }
        }
    }

    private suspend fun getGameMetricRow(
        gameMetric: GameMetric,
    ): ResultRow? {
        val metricId = getMetricId(gameMetric.metric!!)

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
    ): Int {
        val row = getGameMetricRow(gameMetric)!!
        return row[GameMetrics.id].value
    }

    private suspend fun gameMetricExists(
        gameMetric: GameMetric,
    ): Boolean {
        val row = getGameMetricRow(gameMetric)
        return row?.let { true } ?: false
    }

    private suspend fun rowToGameMetric(row: ResultRow): GameMetric {
        val metricId = row[GameMetrics.metric].value

        val metric = null // rowToMetric(getMetricRow(metricId)!!)
        val key = row[GameMetrics.key]
        val value = row[GameMetrics.value]

        return GameMetric(metric, key, value)
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
            insertSeason(season)
        }
    }

    override suspend fun insertSeason(season: Season) {
        if (seasonExists(season)) throw Exception("Season exists.")

        val teamId = getTeamId(season.team!!)

        transaction {
            Seasons.insert {
                it[team] = teamId
                it[year] = season.year
            }
        }

        for (robot in season.robots) {
            insertRobot(robot)
        }

        for (event in season.events) {
            insertSeasonEvent(event, season)
        }
    }

    override suspend fun insertRobot(robot: Robot) {
        if (robotExists((robot))) throw Exception("Robot exists.")

        val seasonId = getSeasonId(robot.season!!)

        transaction {
            Robots.insert {
                it[season] = seasonId
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
            insertMatch(match)
        }
    }

    override suspend fun insertSeasonEvent(event: Event, season: Season) {
        if (!eventExists(event)) insertEvent(event)

        val eventId = getEventId(event)
        val seasonId = getSeasonId(season)

        transaction {
            SeasonEvents.insert {
                it[SeasonEvents.event] = eventId
                it[SeasonEvents.season] = seasonId
            }
        }
    }

    override suspend fun insertMatch(match: Match) {
        if (matchExists(match)) throw Exception("Match exists.")

        val eventId = getEventId(match.event!!)

        transaction {
            Matches.insert {
                it[number] = match.number
                it[type] = match.type
                it[event] = eventId
            }
        }

        for (metric in match.metrics) {
            insertMetric(metric)
        }
    }

    override suspend fun insertMetric(metric: Metric) {
        if (metricExists(metric)) throw Exception("Metric exists.")

        val matchId = getMatchId(metric.match!!)
        val robotId = getRobotId(metric.robot!!)

        transaction {
            Metrics.insert {
                it[match] = matchId
                it[robot] = robotId
                it[alliance] = metric.alliance
            }
        }

        for (gameMetric in metric.gameMetrics) {
            insertGameMetric(gameMetric)
        }
    }

    override suspend fun insertGameMetric(gameMetric: GameMetric) {
        val metricId = getMetricId(gameMetric.metric!!)

        transaction {
            GameMetrics.insert {
                it[metric] = metricId
                it[key] = gameMetric.key
                it[value] = gameMetric.value
            }
        }
    }
}

val db = DatabaseImplementation()
