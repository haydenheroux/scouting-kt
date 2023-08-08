package io.github.haydenheroux.scouting.models.event

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.models.enums.Region
import io.github.haydenheroux.scouting.models.match.*
import io.github.haydenheroux.scouting.models.team.Seasons
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select

object Events : IntIdTable() {
    val name = varchar("name", 255)
    val region = enumerationByName<Region>("region", 255)
    val year = integer("year")
    val week = integer("week")
}

object SeasonEvents : Table() {
    val season = reference("season_id", Seasons)
    val event = reference("event_id", Events)

    override val primaryKey = PrimaryKey(season, event, name = "seasonEvent")
}

@Serializable
data class EventProperties(
    val name: String,
    val region: Region,
    val year: Int,
    val week: Int,
)

fun ResultRow.eventProperties(): EventProperties {
    val name = this[Events.name]
    val region = this[Events.region]
    val year = this[Events.year]
    val week = this[Events.week]

    return EventProperties(name, region, year, week)
}

data class EventReference(
    val name: String,
    val region: Region,
    val year: Int,
    val week: Int,
    val matchReferences: List<MatchReference>
)

suspend fun ResultRow.asEventReference(noChildren: Boolean): EventReference {
    val properties = this.eventProperties()

    val eventId = this[Events.id]
    val matchReferences = if (noChildren) listOf() else query {
        Matches.select { Matches.event eq eventId }.map { it.asMatchReference(false, false) }
    }

    return EventReference(properties.name, properties.region, properties.year, properties.week, matchReferences)
}

fun EventReference.dereference(): Event {
    val matches = matchReferences.map { it.dereference() }
    return Event(name, region, year, week, matches)
}

@Serializable
data class Event(
    val name: String,
    val region: Region,
    val year: Int,
    val week: Int,
    val matches: List<Match>
)
