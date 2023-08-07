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
data class EventData(
    val name: String,
    val region: Region,
    val year: Int,
    val week: Int,
)

fun ResultRow.asEventData(): EventData {
    val name = this[Events.name]
    val region = this[Events.region]
    val year = this[Events.year]
    val week = this[Events.week]

    return EventData(name, region, year, week)
}

data class EventReference(val eventData: EventData, val matchReferences: List<MatchReference>)

suspend fun ResultRow.asEventReference(noChildren: Boolean): EventReference {
    val eventData = this.asEventData()

    val eventId = this[Events.id]
    val matchReferences = if (noChildren) listOf() else query {
        Matches.select { Matches.event eq eventId }.map { it.asMatchReference(false, false) }
    }

    return EventReference(eventData, matchReferences)
}

fun EventReference.dereference(): Event {
    val matches = matchReferences.map { it.dereference() }
    return Event(eventData, matches)
}

@Serializable
data class Event(
    val eventData: EventData,
    val matches: List<Match>
)
