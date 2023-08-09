package io.github.haydenheroux.scouting.models.event

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.Region
import io.github.haydenheroux.scouting.models.enums.regionOf
import io.github.haydenheroux.scouting.models.match.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

object EventTable : IntIdTable() {
    val name = varchar("name", 255)
    val region = enumerationByName<Region>("region", 255)
    val year = integer("year")
    val week = integer("week")
}

@Serializable
data class EventProperties(
    val name: String,
    val region: Region,
    val year: Int,
    val week: Int,
)

fun ResultRow.eventProperties(): EventProperties {
    val name = this[EventTable.name]
    val region = this[EventTable.region]
    val year = this[EventTable.year]
    val week = this[EventTable.week]

    return EventProperties(name, region, year, week)
}

data class EventReference(
    val eventId: Int,
    val name: String,
    val region: Region,
    val year: Int,
    val week: Int,
)

fun ResultRow.eventReference(): EventReference {
    val eventId = this[EventTable.id].value
    val properties = this.eventProperties()

    return EventReference(eventId, properties.name, properties.region, properties.year, properties.week)
}

suspend fun EventReference.dereference(children: Boolean): Event {
    val matches = if (children) db.getMatches(this).map { it.dereference(true) } else emptyList()
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

data class EventQuery(val name: String, val region: Region, val year: Int, val week: Int)

fun Event.query(): EventQuery {
    return EventQuery(name, region, year, week)
}

fun Parameters.eventQuery(): Result<EventQuery> {
    val name = this["event"] ?: return Result.failure(Exception("Missing `event` in parameters"))
    val region = regionOf[this["region"]] ?: return Result.failure(Exception("Missing `region` in parameters"))
    val year = this["year"] ?: return Result.failure(Exception("Missing `year` in parameters"))
    val week = this["week"] ?: return Result.failure(Exception("Missing `week` in parameters"))

    return Result.success(EventQuery(name, region, year.toInt(), week.toInt()))
}
