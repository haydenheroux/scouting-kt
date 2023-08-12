package io.github.haydenheroux.scouting.models.event

import io.github.haydenheroux.scouting.database.sql.db
import io.github.haydenheroux.scouting.database.sql.tree.Branch
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Parent
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.models.enums.Region
import io.github.haydenheroux.scouting.models.enums.regionOf
import io.github.haydenheroux.scouting.models.match.Match
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
    Node<Tree<Event>, Event> {

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

    override suspend fun parent(): Parent<Tree<Event>, Event>? {
        return null
    }

    override suspend fun branch(): Branch<Tree<Event>, Event> {
        val match = db.getMatchesByEvent(this).getOrNull()!!

        return EventBranch(this, match)
    }

    override fun tree(): Tree<Event> {
        return EventTree(this, emptyList())
    }
}

data class EventBranch(val event: EventNode, val matches: List<Node<Tree<Match>, Match>>) :
    Branch<Tree<Event>, Event> {
    override suspend fun parent(): Parent<Tree<Event>, Event>? {
        return event.parent()
    }

    override suspend fun tree(): Tree<Event> {
        val matches = matches.map { it.branch() }

        return EventTree(event, matches)
    }
}

data class EventTree(val event: EventNode, val matches: List<Branch<Tree<Match>, Match>>) : Tree<Event> {
    override fun leaf(): Event {
        return Event(event.name, event.region, event.year, event.week, emptyList())
    }

    override suspend fun leaves(): Event {
        val matches = matches.map { match -> match.tree().leaf() }

        return Event(event.name, event.region, event.year, event.week, matches)
    }

    override suspend fun subtree(): Event {
        val matches = matches.map { match -> match.tree().subtree() }

        return Event(event.name, event.region, event.year, event.week, matches)
    }

    override suspend fun subtree(depth: Int): Event {
        if (depth == 0) return leaf()
        if (depth == 1) return leaves()

        val matches = matches.map { match -> match.tree().subtree(depth - 1) }

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
