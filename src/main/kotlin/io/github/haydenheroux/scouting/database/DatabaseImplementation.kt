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
            Teams.selectAll().map { it.asTeamReference(false) }
        }
    }

    override suspend fun getTeam(teamQuery: TeamQuery): TeamReference {
        println(teamQuery)
        return getTeamRow(teamQuery)!!.asTeamReference(false)
    }

    private suspend fun getTeamRow(teamQuery: TeamQuery): ResultRow? {
        return query {
            Teams.select { Teams.number eq teamQuery.number }.singleOrNull()
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

    private fun rowToTeamQuery(teamRow: ResultRow): TeamQuery {
        val teamNumber = teamRow[Teams.number]

        return TeamQuery(teamNumber)
    }

    override suspend fun getSeason(seasonQuery: SeasonQuery): SeasonReference {
        return getSeasonRow(seasonQuery)!!.asSeasonReference(false, false)
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

    private suspend fun rowToSeasonQuery(seasonRow: ResultRow): SeasonQuery {
        val year = seasonRow[Seasons.year]

        val teamQuery = rowToTeamQuery(getTeamRow(seasonRow[Seasons.team].value)!!)

        return SeasonQuery(year, teamQuery)
    }

    override suspend fun getRobot(robotQuery: RobotQuery): RobotReference {
        return getRobotRow(robotQuery)!!.asRobotReference(false)
    }

    private suspend fun getRobotRow(robotQuery: RobotQuery): ResultRow? {
        val seasonId = getSeasonId(robotQuery.season)

        return query {
            Robots.select { (Robots.name eq robotQuery.name) and (Robots.season eq seasonId) }.singleOrNull()
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

    private suspend fun rowToRobotQuery(robotRow: ResultRow): RobotQuery {
        val name = robotRow[Robots.name]

        val season = rowToSeasonQuery(getSeasonRow(robotRow[Robots.season].value)!!)

        return RobotQuery(name, season)
    }

    override suspend fun getEvents(): List<EventReference> {
        return query {
            Events.selectAll().map { it.asEventReference(false) }
        }
    }

    override suspend fun getEvent(eventQuery: EventQuery): EventReference {
        return getEventRow(eventQuery)!!.asEventReference(false)
    }

    private suspend fun getEventRow(eventQuery: EventQuery): ResultRow? {
        return query {
            Events.select { (Events.name eq eventQuery.name) and (Events.region eq eventQuery.region) and (Events.year eq eventQuery.year) and (Events.week eq eventQuery.week) }
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

    private fun rowToEventQuery(eventRow: ResultRow): EventQuery {
        val name = eventRow[Events.name]
        val region = eventRow[Events.region]
        val year = eventRow[Events.year]
        val week = eventRow[Events.week]

        return EventQuery(name, region, year, week)
    }

    override suspend fun getMatch(matchQuery: MatchQuery): MatchReference {
        return getMatchRow(matchQuery)!!.asMatchReference(false, false)
    }

    private suspend fun getMatchRow(matchQuery: MatchQuery): ResultRow? {
        val eventId = getEventId(matchQuery.event)

        return query {
            Matches.select { (Matches.event eq eventId) and (Matches.number eq matchQuery.number) }
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

    private suspend fun rowToMatchQuery(matchRow: ResultRow): MatchQuery {
        val matchNumber = matchRow[Matches.number]

        val eventQuery = rowToEventQuery(getEventRow(matchRow[Matches.event].value)!!)

        return MatchQuery(matchNumber, eventQuery)
    }

    override suspend fun getMetric(metricQuery: MetricQuery): MetricReference {
        return getMetricRow(metricQuery)!!.asMetricReference(false, false)
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

    private suspend fun rowToMetricQuery(metricRow: ResultRow): MetricQuery {
        val match = rowToMatchQuery(getMatchRow(metricRow[Metrics.match].value)!!)
        val robot = rowToRobotQuery(getRobotRow(metricRow[Metrics.robot].value)!!)

        return MetricQuery(match, robot)
    }

    override suspend fun insertTeam(team: Team) {
        if (teamExists(team.query())) throw Exception("Team exists")

        transaction {
            Teams.insert {
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
            Seasons.insert {
                it[team] = teamId
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
            SeasonEvents.insert {
                it[season] = seasonId
                it[event] = eventId
            }
        }
    }

    override suspend fun insertRobot(robot: Robot, seasonQuery: SeasonQuery) {
        if (robotExists(robot.query(seasonQuery))) throw Exception("Robot exists")

        val seasonId = getSeasonId(seasonQuery)

        transaction {
            Robots.insert {
                it[season] = seasonId
                it[name] = robot.name
            }
        }
    }

    override suspend fun insertEvent(event: Event) {
        if (eventExists(event.query())) throw Exception("Event exists")

        transaction {
            Events.insert {
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
            Matches.insert {
                it[event] = eventId
                it[number] = match.number
                it[type] = match.type
            }
        }

        for (metric in match.metrics) {
            // TODO
            // insertMetric(metric, match.query(eventQuery))
        }
    }

    override suspend fun insertMetric(metric: Metric, matchQuery: MatchQuery, robotQuery: RobotQuery) {
        if (metricExists(MetricQuery(matchQuery, robotQuery))) throw Exception("Metric exists")

        val matchId = getMatchId(matchQuery)
        val robotId = getRobotId(robotQuery)

        transaction {
            val metricId = Metrics.insertAndGetId {
                it[match] = matchId
                it[robot] = robotId
                it[alliance] = metric.alliance
            }

            // TODO Potential bug involving duplication of game metrics
            for (gameMetric in metric.gameMetrics) {
                GameMetrics.insert {
                    it[GameMetrics.metric] = metricId
                    it[key] = gameMetric.key
                    it[value] = gameMetric.value
                }
            }
        }

    }
}

val db = DatabaseImplementation()
