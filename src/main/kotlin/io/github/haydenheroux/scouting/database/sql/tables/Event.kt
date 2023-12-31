package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.excludes.Exclude
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.errors.Error
import io.github.haydenheroux.scouting.errors.Success
import io.github.haydenheroux.scouting.models.Event
import io.github.haydenheroux.scouting.models.Match
import io.github.haydenheroux.scouting.models.enums.Region
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object EventTable : IntIdTable() {
    val code = varchar("code", 255)
    val name = varchar("name", 255)
    val region = enumerationByName<Region>("region", 255)
    val year = integer("year")
    val week = integer("week")
}

data class EventNode(
    val id: Int,
    val code: String,
    val name: String,
    val region: Region,
    val year: Int,
    val week: Int
) :
    Node<Tree<Event>, Event> {

    companion object {
        fun from(eventRow: ResultRow): EventNode {
            return EventNode(
                eventRow[EventTable.id].value,
                eventRow[EventTable.code],
                eventRow[EventTable.name],
                eventRow[EventTable.region],
                eventRow[EventTable.year],
                eventRow[EventTable.week]
            )
        }
    }

    override suspend fun tree(parent: Boolean, excludes: List<Exclude>): Tree<Event> {
        val matchesOrError =
            if (Exclude.EVENT_MATCHES in excludes) Success(emptyList()) else SQLDatabase.getMatchesByEvent(this)

        val matches = when (matchesOrError) {
            is Success -> matchesOrError.value
            is Error -> null
        }

        return EventTree(this, matches!!)
    }

    override fun leaf(): Event {
        return createEvent(this, emptyList())
    }
}

data class EventTree(val event: EventNode, val matches: List<MatchNode>) : Tree<Event> {

    override suspend fun subtree(): Event {
        val matches = matches.map { match -> match.tree(false, emptyList()).subtree() }

        return createEvent(event, matches)
    }

    override suspend fun subtree(depth: Int, excludes: List<Exclude>): Event {
        if (depth == 0) return event.leaf()

        val matches = if (Exclude.EVENT_MATCHES in excludes) emptyList() else matches.map { match ->
            match.tree(false, excludes).subtree(depth - 1, excludes)
        }

        return createEvent(event, matches)
    }
}

fun createEvent(event: EventNode, matches: List<Match>): Event {
    return Event(event.code, event.name, event.region, event.year, event.week, matches)
}