package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.match.*
import io.github.haydenheroux.scouting.models.team.Robot
import io.github.haydenheroux.scouting.models.team.Season
import io.github.haydenheroux.scouting.models.team.Team
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Teams : IntIdTable() {
    val number = integer("number")
    val name = varchar("name", 255)
    val location = varchar("location", 255)
}

object Seasons : IntIdTable() {
    val team = reference("team_id", Teams)
    val year = integer("year")
}

object Robots : IntIdTable() {
    val season = reference("season_id", Seasons)
    val name = varchar("name", 255)
}

object Metrics : IntIdTable() {
    val robot = reference("robot_id", Robots)
    val alliance = enumerationByName<Alliance>("alliance", 255)
}

object GameMetrics : IntIdTable() {
    val metric = reference("metric_id", Metrics)
    val key = varchar("key", 255)
    val value = varchar("value", 255)
}

object Matches : IntIdTable() {
    val number = integer("number")
    val type = enumerationByName<MatchType>("type", 255)
    val event = reference("event_id", Events)
}

object Events : IntIdTable() {
    val name = varchar("name", 255)
    val location = varchar("location", 255)
    val year = integer("year")
    val week = integer("week")
}

object SeasonEvents : Table() {
    val season = reference("season_id", Seasons)
    val event = reference("event_id", Events)

    override val primaryKey = PrimaryKey(season, event, name = "seasonEvent")
}

suspend fun ResultRow.toTeam(): Team {
    val teamId = this[Teams.id].value
    val number = this[Teams.number]
    val name = this[Teams.name]
    val location = this[Teams.location]
    val seasons = db.fetchSeasonsByTeamId(teamId)
    return Team(number, name, location, seasons)
}

suspend fun ResultRow.toSeason(): Season {
    val seasonId = this[Seasons.id].value
    val year = this[Seasons.year]
    val robots = db.fetchRobotsBySeasonId(seasonId)
    val events = db.fetchEventsBySeasonId(seasonId)
    return Season(year, robots, events)
}

fun ResultRow.toRobot(): Robot {
    val name = this[Robots.name]
    return Robot(name)
}

suspend fun ResultRow.toMetric(): Metric {
    val metricId = this[Metrics.id].value
    val robotId = this[Metrics.robot].value
    val robot = db.fetchRobotById(robotId)
    val alliance = this[Metrics.alliance]
    val gameMetrics = db.fetchGameMetricsByMetricId(metricId)
    return Metric(robot, alliance, gameMetrics)
}

suspend fun ResultRow.toMatch(): Match {
    val matchId = this[Matches.id].value
    val number = this[Matches.number]
    val type = this[Matches.type]
    val metrics = db.fetchMetricsByMatchId(matchId)
    return Match(number, type, metrics)
}

fun ResultRow.toGameMetric(): GameMetric {
    val key = this[GameMetrics.key]
    val value = this[GameMetrics.value]
    return GameMetric(key, value)
}

fun ResultRow.toEvent(): Event {
    val name = this[Events.name]
    val location = this[Events.location]
    val year = this[Events.year]
    val week = this[Events.week]
    val matches = emptyList<Match>() // Fetch matches from database based on event ID
    return Event(name, location, year, week, matches)
}
