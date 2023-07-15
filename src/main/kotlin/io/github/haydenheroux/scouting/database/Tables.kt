package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.models.match.AllianceColor
import io.github.haydenheroux.scouting.models.match.MatchType
import org.jetbrains.exposed.dao.id.IntIdTable

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
    val alliance = enumerationByName<AllianceColor>("alliance", 255)
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
