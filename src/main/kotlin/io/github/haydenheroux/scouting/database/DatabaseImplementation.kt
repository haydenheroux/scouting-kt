package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.models.enums.Region
import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.event.Events
import io.github.haydenheroux.scouting.models.event.SeasonEvents
import io.github.haydenheroux.scouting.models.event.toEvent
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

    private suspend fun getTeamId(team: Team): Int {
        val row: ResultRow = getTeamRow(team)!!
        return row[Teams.id].value
    }

    private suspend fun teamExists(team: Team): Boolean {
        val row = getTeamRow(team)
        return row?.let { true } ?: false
    }

    private suspend fun getTeamRow(team: Team): ResultRow? {
        return getTeamRowByNumber(team.number)
    }

    private suspend fun getTeamRowByNumber(number: Int): ResultRow? {
        return query {
            Teams.select { Teams.number eq number }.singleOrNull()
        }
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
            Seasons.select { Seasons.team eq teamId }.map { it.toSeason() }
        }
    }

    override suspend fun getEvents(): List<Event> {
        return query {
            Events.selectAll().map { it.toEvent() }
        }
    }

    override suspend fun getEventsByRegion(region: Region): List<Event> {
        return query {
            Events.select { Events.region eq region }.map { it.toEvent() }
        }
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

    private suspend fun seasonExists(season: Season, team: Team): Boolean {
        val teamId = getTeamId(team)

        return query {
            !Seasons.select { (Seasons.year eq season.year) and (Seasons.team eq teamId) }.empty()
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

    private suspend fun robotExists(robot: Robot, season: Season, team: Team): Boolean {
        val seasonId = findSeasonId(season, team)

        return query {
            !Robots.select { (Robots.name eq robot.name) and (Robots.season eq seasonId) }.empty()
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

    private suspend fun eventExists(event: Event): Boolean {
        return query {
            !Events.select { (Events.name eq event.name) and (Events.year eq event.year) and (Events.week eq event.week) }
                .empty()
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

    private suspend fun matchExists(match: Match, event: Event): Boolean {
        val eventId = findEventId(event)

        return query {
            !Matches.select { (Matches.event eq eventId) and (Matches.number eq match.number) and (Matches.type eq match.type) }
                .empty()
        }
    }

    override suspend fun insertMetric(metric: Metric, match: Match, event: Event, season: Season, team: Team) {
        if (metricExists(metric, match, event, season, team)) throw Exception("Metric exists.")

        TODO()
    }

    private suspend fun metricExists(metric: Metric, match: Match, event: Event, season: Season, team: Team): Boolean {
        val matchId = findMatchId(match, event)
        val robotId = findRobotId(metric.robot, season, team)

        val matchingMetrics = query {
            Metrics.select { (Metrics.match eq matchId) and (Metrics.robot eq robotId) }
        }

        return !matchingMetrics.empty()
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

    override suspend fun findSeason(seasonId: Int): Season {
        return query {
            Seasons.select { Seasons.id eq seasonId }.map { it.toSeason() }[0]
        }
    }

    override suspend fun findRobot(robotId: Int): Robot {
        return query {
            Robots.select { Robots.id eq robotId }.map { it.toRobot() }[0]
        }
    }

    override suspend fun findRobots(seasonId: Int): List<Robot> {
        return query {
            Robots.select { Robots.season eq seasonId }.map { it.toRobot() }
        }
    }

    override suspend fun findEvents(seasonId: Int): List<Event> {
        return getEventIdsBySeasonId(seasonId).map { fetchEventById(it) }
    }

    private suspend fun fetchEventById(eventId: Int): Event {
        return query {
            Events.select { Events.id eq eventId }.map { it.toEvent() }[0]
        }
    }

    private suspend fun getEventIdsBySeasonId(seasonId: Int): List<Int> {
        return query {
            SeasonEvents.select { SeasonEvents.season eq seasonId }.map { it[SeasonEvents.event].value }
        }
    }

    override suspend fun findMatches(eventId: Int): List<Match> {
        return query {
            Matches.select { Matches.event eq eventId }.map { it.toMatch() }
        }
    }

    override suspend fun findMetrics(matchId: Int): List<Metric> {
        return query {
            Metrics.select { Metrics.match eq matchId }.map { it.toMetric() }
        }
    }

    override suspend fun findGameMetrics(metricId: Int): List<GameMetric> {
        return query {
            GameMetrics.select { GameMetrics.metric eq metricId }.map { it.toGameMetric() }
        }
    }
}

val db = DatabaseImplementation()
