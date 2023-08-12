package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.tree.Branch
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.models.Event
import io.github.haydenheroux.scouting.models.Match
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
    Node<Branch<Event>, Event> {

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

    override suspend fun tree(): Tree<Branch<Event>, Event> {
        val match = SQLDatabase.getMatchesByEvent(this).getOrNull()!!

        return EventTree(this, match)
    }

    override fun root(): Branch<Event> {
        return EventBranch(this, emptyList())
    }
}

data class EventTree(val event: EventNode, val matches: List<Node<Branch<Match>, Match>>) :
    Tree<Branch<Event>, Event> {
    override suspend fun branch(): Branch<Event> {
        val matches = matches.map { it.tree() }

        return EventBranch(event, matches)
    }
}

data class EventBranch(val event: EventNode, val matches: List<Tree<Branch<Match>, Match>>) : Branch<Event> {
    override fun leaf(): Event {
        return Event(event.name, event.region, event.year, event.week, emptyList())
    }

    override suspend fun leaves(): Event {
        val matches = matches.map { match -> match.branch().leaf() }

        return Event(event.name, event.region, event.year, event.week, matches)
    }

    override suspend fun subbranch(): Event {
        val matches = matches.map { match -> match.branch().subbranch() }

        return Event(event.name, event.region, event.year, event.week, matches)
    }

    override suspend fun subbranch(depth: Int): Event {
        if (depth == 0) return leaf()
        if (depth == 1) return leaves()

        val matches = matches.map { match -> match.branch().subbranch(depth - 1) }

        return Event(event.name, event.region, event.year, event.week, matches)
    }
}

