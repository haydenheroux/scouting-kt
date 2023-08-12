package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.models.Event
import io.github.haydenheroux.scouting.models.enums.Region
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

    override suspend fun tree(): Tree<Event> {
        val matches = SQLDatabase.getMatchesByEvent(this).getOrNull()!!

        return EventTree(this, matches)
    }

    override fun leaf(): Event {
        return Event(name, region, year, week, emptyList())
    }
}

data class EventTree(val event: EventNode, val matches: List<MatchNode>) : Tree<Event> {
    override suspend fun leaves(): Event {
        val matches = matches.map { match -> match.leaf() }

        return Event(event.name, event.region, event.year, event.week, matches)
    }

    override suspend fun subtree(): Event {
        val matches = matches.map { match -> match.tree().subtree() }

        return Event(event.name, event.region, event.year, event.week, matches)
    }

    override suspend fun subtree(depth: Int): Event {
        if (depth == 0) return event.leaf()
        if (depth == 1) return leaves()

        val matches = matches.map { match -> match.tree().subtree(depth - 1) }

        return Event(event.name, event.region, event.year, event.week, matches)
    }
}

