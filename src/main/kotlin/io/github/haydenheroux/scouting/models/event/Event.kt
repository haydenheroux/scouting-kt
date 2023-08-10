package io.github.haydenheroux.scouting.models.event

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.Region
import io.github.haydenheroux.scouting.models.enums.regionOf
import io.github.haydenheroux.scouting.models.interfaces.Node
import io.github.haydenheroux.scouting.models.interfaces.Parent
import io.github.haydenheroux.scouting.models.interfaces.Subtree
import io.github.haydenheroux.scouting.models.match.Match
import io.github.haydenheroux.scouting.models.match.MatchTree
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

data class EventNode(val id: Int, val name: String, val region: Region, val year: Int, val week: Int) :
    Node<EventTree> {

    companion object {
        fun from(eventRow: ResultRow): EventNode {
            return EventNode(
                eventRow[EventTable.id].value,
                eventRow[EventTable.name],
                eventRow[EventTable.region],
                eventRow[EventTable.year],
                eventRow[EventTable.week]
            )
        }
    }

    override suspend fun parent(): Parent<EventTree>? {
        return null
    }

    override suspend fun subtree(): Subtree<EventTree> {
        val match = db.getMatchesByEvent(this)

        return EventSubtree(this, match)
    }

    override fun tree(): EventTree {
        return EventTree(this, emptyList())
    }
}

data class EventSubtree(val event: EventNode, val matches: List<Node<MatchTree>>) : Subtree<EventTree> {
    override suspend fun parent(): Parent<EventTree>? {
        return event.parent()
    }

    override suspend fun tree(): EventTree {
        val matches = matches.map { it.subtree() }

        return EventTree(event, matches)
    }
}

data class EventTree(val event: EventNode, val matches: List<Subtree<MatchTree>>) {
    fun noChildren(): Event {
        return Event(event.name, event.region, event.year, event.week, emptyList())
    }

    suspend fun children(): Event {
        val matches = matches.map { match -> match.tree().noChildren() }

        return Event(event.name, event.region, event.year, event.week, matches)
    }

    suspend fun subChildren(): Event {
        val matches = matches.map { match -> match.tree().subChildren() }

        return Event(event.name, event.region, event.year, event.week, matches)
    }
}

@Serializable
data class Event(val name: String, val region: Region, val year: Int, val week: Int, val matches: List<Match>)

data class EventQuery(val name: String, val region: Region, val year: Int, val week: Int)

fun eventQueryOf(event: Event): EventQuery {
    return EventQuery(event.name, event.region, event.year, event.week)
}

fun Parameters.eventQuery(): Result<EventQuery> {
    val name = this["event"] ?: return Result.failure(Exception("Missing `event` in parameters"))
    val region = regionOf[this["region"]] ?: return Result.failure(Exception("Missing `region` in parameters"))
    val year = this["year"] ?: return Result.failure(Exception("Missing `year` in parameters"))
    val week = this["week"] ?: return Result.failure(Exception("Missing `week` in parameters"))

    return Result.success(EventQuery(name, region, year.toInt(), week.toInt()))
}
