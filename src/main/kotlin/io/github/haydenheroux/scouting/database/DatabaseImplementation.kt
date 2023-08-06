package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.event.Events
import io.github.haydenheroux.scouting.models.event.SeasonEvents
import io.github.haydenheroux.scouting.models.match.*
import io.github.haydenheroux.scouting.models.team.*
import io.github.haydenheroux.scouting.query.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseImplementation : DatabaseInterface {

    override suspend fun getTeams(): List<Team> {
        return query {
            Teams.selectAll().map { rowToTeam(it) }
        }
    }

    override suspend fun getTeam(teamQuery: TeamQuery): Team {
        return rowToTeam(getTeamRow(teamQuery)!!)
    }

    private suspend fun getTeamRow(teamQuery: TeamQuery): ResultRow? {
        return query {
            Teams.select { Teams.number eq teamQuery.teamNumber }.singleOrNull()
        }
    }

    private suspend fun getTeamRow(teamId: Int): ResultRow? {
        return query {
            Teams.select { Teams.id eq teamId }.singleOrNull()
        }
    }

    private suspend fun getTeamId(teamQuery: TeamQuery): Int {
        return getTeamRow(teamQuery)!![Teams.id].value
    }

    private suspend fun teamExists(teamQuery: TeamQuery): Boolean {
        return getTeamRow(teamQuery)?.let { true } ?: false
    }

    private suspend fun rowToTeam(teamRow: ResultRow): Team {
        val teamId = teamRow[Teams.id].value

        val number = teamRow[Teams.number]
        val name = teamRow[Teams.name]
        val region = teamRow[Teams.region]
        val seasons = getSeasonsForTeam(teamId)

        return Team(number, name, region, seasons)
    }

    private suspend fun getSeasonsForTeam(teamId: Int): List<Season> {
        return query {
            Seasons.select { Seasons.team eq teamId }.map { rowToSeason(it) }
        }
    }

    override suspend fun getSeason(seasonQuery: SeasonQuery): Season {
        val season = rowToSeason(getSeasonRow(seasonQuery)!!)
        season.team = seasonQuery.team

        return season
    }

    private suspend fun getSeasonRow(seasonQuery: SeasonQuery): ResultRow? {
        val teamId = getTeamId(seasonQuery.team)

        return query {
            Seasons.select { (Seasons.year eq seasonQuery.year) and (Seasons.team eq teamId) }.singleOrNull()
        }
    }

    private suspend fun getSeasonRow(seasonId: Int): ResultRow? {
        return query {
            Seasons.select { Seasons.id eq seasonId }.singleOrNull()
        }
    }

    private suspend fun getSeasonId(seasonQuery: SeasonQuery): Int {
        return getSeasonRow(seasonQuery)!![Seasons.id].value
    }

    private suspend fun seasonExists(seasonQuery: SeasonQuery): Boolean {
        return getSeasonRow(seasonQuery)?.let { true } ?: false
    }

    private suspend fun rowToSeason(seasonRow: ResultRow): Season {
        val seasonId: Int = seasonRow[Seasons.id].value

        val team = null // rowToTeam(getTeamRow(row[Seasons.team].value)!!)
        val year = seasonRow[Seasons.year]
        val robots = getRobotsForSeason(seasonId)
        val events = getEventsForSeason(seasonId)

        return Season(team, year, robots, events)
    }

    private suspend fun getRobotsForSeason(seasonId: Int): List<Robot> {
        return query {
            Robots.select { Robots.season eq seasonId }.map { rowToRobot(it) }
        }
    }

    override suspend fun getRobot(robotQuery: RobotQuery): Robot {
        val robot = rowToRobot(getRobotRow(robotQuery)!!)
        robot.season = robotQuery.season

        return robot
    }

    private suspend fun getRobotRow(robotQuery: RobotQuery): ResultRow? {
        val seasonId = getSeasonId(robotQuery.season)

        return query {
            Robots.select { (Robots.name eq robotQuery.robotName) and (Robots.season eq seasonId) }.singleOrNull()
        }
    }

    private suspend fun getRobotRow(robotId: Int): ResultRow? {
        return query {
            Robots.select { Robots.id eq robotId }.singleOrNull()
        }
    }

    private suspend fun getRobotId(robotQuery: RobotQuery): Int {
        return getRobotRow(robotQuery)!![Robots.id].value
    }

    private suspend fun robotExists(robotQuery: RobotQuery): Boolean {
        return getRobotRow(robotQuery)?.let { true } ?: false
    }

    private fun rowToRobot(robotRow: ResultRow): Robot {
        val season = null
        val name = robotRow[Robots.name]

        return Robot(season, name)
    }

    override suspend fun getEvents(): List<Event> {
        return query {
            Events.selectAll().map { rowToEvent(it) }
        }
    }

    override suspend fun getEvent(eventQuery: EventQuery): Event {
        return rowToEvent(getEventRow(eventQuery)!!)
    }

    private suspend fun getEventRow(eventQuery: EventQuery): ResultRow? {
        return query {
            Events.select { (Events.name eq eventQuery.eventName) and (Events.region eq eventQuery.region) and (Events.year eq eventQuery.year) and (Events.week eq eventQuery.week) }
                .singleOrNull()
        }
    }

    private suspend fun getEventRow(eventId: Int): ResultRow? {
        return query {
            Events.select { Events.id eq eventId }.singleOrNull()
        }
    }

    private suspend fun getEventId(eventQuery: EventQuery): Int {
        return getEventRow(eventQuery)!![Events.id].value
    }

    private suspend fun eventExists(eventQuery: EventQuery): Boolean {
        return getEventRow(eventQuery)?.let { true } ?: false
    }

    private suspend fun rowToEvent(eventRow: ResultRow): Event {
        val eventId: Int = eventRow[Events.id].value

        val name = eventRow[Events.name]
        val region = eventRow[Events.region]
        val year = eventRow[Events.year]
        val week = eventRow[Events.week]
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

    override suspend fun getMatch(matchQuery: MatchQuery): Match {
        val match = rowToMatch(getMatchRow(matchQuery)!!)
        match.event = matchQuery.event

        return match
    }

    private suspend fun getMatchRow(matchQuery: MatchQuery): ResultRow? {
        val eventId = getEventId(matchQuery.event)

        return query {
            Matches.select { (Matches.event eq eventId) and (Matches.number eq matchQuery.matchNumber) }
                .singleOrNull()
        }
    }

    private suspend fun getMatchRow(matchId: Int): ResultRow? {
        return query {
            Matches.select { Matches.id eq matchId }.singleOrNull()
        }
    }

    private suspend fun getMatchId(matchQuery: MatchQuery): Int {
        return getMatchRow(matchQuery)!![Matches.id].value
    }

    private suspend fun matchExists(matchQuery: MatchQuery): Boolean {
        return getMatchRow(matchQuery)?.let { true } ?: false
    }

    private suspend fun rowToMatch(matchRow: ResultRow): Match {
        val matchId = matchRow[Matches.id].value

        val event = null
        val number = matchRow[Matches.number]
        val type = matchRow[Matches.type]
        val metrics = getMetricsForMatch(matchId)

        return Match(event, number, type, metrics)
    }

    private suspend fun getMetricsForMatch(matchId: Int): List<Metric> {
        return query {
            Metrics.select { Metrics.match eq matchId }.map { rowToMetric(it) }
        }
    }

    override suspend fun getMetric(metricQuery: MetricQuery): Metric {
        return rowToMetric(getMetricRow(metricQuery)!!)
    }

    private suspend fun getMetricRow(metricQuery: MetricQuery): ResultRow? {
        val matchId = getMatchId(metricQuery.match)
        val robotId = getRobotId(metricQuery.robot)

        return query {
            Metrics.select { (Metrics.match eq matchId) and (Metrics.robot eq robotId) }.singleOrNull()
        }
    }

    private suspend fun getMetricRow(metricId: Int): ResultRow? {
        return query {
            Metrics.select { Metrics.id eq metricId }.singleOrNull()
        }
    }

    private suspend fun getMetricId(metricQuery: MetricQuery): Int {
        return getMetricRow(metricQuery)!![Metrics.id].value
    }

    private suspend fun metricExists(metricQuery: MetricQuery): Boolean {
        return getMetricRow(metricQuery)?.let { true } ?: false
    }

    private suspend fun rowToMetric(metricRow: ResultRow): Metric {
        val metricId = metricRow[Metrics.id].value

        val match = null
        val robot = null
        val alliance = metricRow[Metrics.alliance]
        val gameMetrics = getGameMetricsForMetric(metricId)

        return Metric(match, robot, alliance, gameMetrics)
    }

    private suspend fun getGameMetricsForMetric(metricId: Int): List<GameMetric> {
        return query {
            GameMetrics.select { GameMetrics.metric eq metricId }.map { rowToGameMetric(it) }
        }
    }

    private suspend fun getGameMetricRow(gameMetric: GameMetric): ResultRow? {
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

    private suspend fun getGameMetricId(gameMetric: GameMetric): Int {
        return getGameMetricRow(gameMetric)!![GameMetrics.id].value
    }

    private suspend fun gameMetricExists(gameMetric: GameMetric): Boolean {
        return getGameMetricRow(gameMetric)?.let { true } ?: false
    }

    private fun rowToGameMetric(gameMetricRow: ResultRow): GameMetric {
        val metric = null
        val key = gameMetricRow[GameMetrics.key]
        val value = gameMetricRow[GameMetrics.value]

        return GameMetric(metric, key, value)
    }

    override suspend fun insertTeam(team: Team) {
        if (teamExists(teamQueryFromTeam(team))) throw Exception("Team exists.")

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
        if (seasonExists(seasonQueryFromSeason(season))) throw Exception("Season exists.")

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
        if (robotExists(robotQueryFromRobot(robot))) throw Exception("Robot exists.")

        val seasonId = getSeasonId(robot.season!!)

        transaction {
            Robots.insert {
                it[season] = seasonId
                it[name] = robot.name
            }
        }
    }

    override suspend fun insertEvent(event: Event) {
        if (eventExists(eventQueryFromEvent(event))) throw Exception("Event exists.")

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
        if (!eventExists(eventQueryFromEvent(event))) insertEvent(event)

        val eventId = getEventId(eventQueryFromEvent(event))
        val seasonId = getSeasonId(seasonQueryFromSeason(season))

        transaction {
            SeasonEvents.insert {
                it[SeasonEvents.event] = eventId
                it[SeasonEvents.season] = seasonId
            }
        }
    }

    override suspend fun insertMatch(match: Match) {
        if (matchExists(matchQueryFromMatch(match))) throw Exception("Match exists.")

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
        if (metricExists(metricQueryFromMetric(metric))) throw Exception("Metric exists.")

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
