package io.github.haydenheroux.scouting.models.event

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.Region
import io.github.haydenheroux.scouting.models.enums.regionOf
import io.github.haydenheroux.scouting.models.interfaces.Data
import io.github.haydenheroux.scouting.models.interfaces.Parented
import io.github.haydenheroux.scouting.models.interfaces.Reference
import io.github.haydenheroux.scouting.models.match.Match
import io.github.haydenheroux.scouting.models.match.MatchDTO
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object EventTable : IntIdTable() {
    val name = varchar("name", 255)
    val region = enumerationByName<Region>("region", 255)
    val year = integer("year")
    val week = integer("week")
}

data class EventData(val eventId: Int, val name: String, val region: Region, val year: Int, val week: Int) :
    Data<Event> {

    companion object {
        fun from(eventRow: ResultRow): EventData {
            return EventData(
                eventRow[EventTable.id].value,
                eventRow[EventTable.name],
                eventRow[EventTable.region],
                eventRow[EventTable.year],
                eventRow[EventTable.week]
            )
        }
    }

    override suspend fun parent(): Parented<Event>? {
        return null
    }

    override suspend fun reference(): Reference<Event> {
        val matchData = db.getMatchesByEvent(this)

        return EventReference(this, matchData)
    }

    override fun data(): Event {
        return Event(this, emptyList())
    }
}

data class EventReference(val eventData: EventData, val matchData: List<Data<Match>>) : Reference<Event> {
    override suspend fun parent(): Parented<Event>? {
        return eventData.parent()
    }

    override suspend fun dereference(): Event {
        val matchReferences = matchData.map { it.reference() }

        return Event(eventData, matchReferences)
    }
}

data class Event(val eventData: EventData, val matchReferences: List<Reference<Match>>)

@Serializable
data class EventDTO(val name: String, val region: Region, val year: Int, val week: Int, val matches: List<MatchDTO>)

data class EventQuery(val name: String, val region: Region, val year: Int, val week: Int)

fun eventQueryOf(eventDTO: EventDTO): EventQuery {
    return EventQuery(eventDTO.name, eventDTO.region, eventDTO.year, eventDTO.week)
}

fun Parameters.eventQuery(): Result<EventQuery> {
    val name = this["event"] ?: return Result.failure(Exception("Missing `event` in parameters"))
    val region = regionOf[this["region"]] ?: return Result.failure(Exception("Missing `region` in parameters"))
    val year = this["year"] ?: return Result.failure(Exception("Missing `year` in parameters"))
    val week = this["week"] ?: return Result.failure(Exception("Missing `week` in parameters"))

    return Result.success(EventQuery(name, region, year.toInt(), week.toInt()))
}
