package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.excludes.Exclude
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.errors.Error
import io.github.haydenheroux.scouting.errors.Success
import io.github.haydenheroux.scouting.models.Alliance
import io.github.haydenheroux.scouting.models.Match
import io.github.haydenheroux.scouting.models.enums.MatchType
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object MatchTable : IntIdTable() {
    val eventId = reference("eventId", EventTable)
    val set = integer("set")
    val number = integer("number")
    val type = enumerationByName<MatchType>("type", 255)
}

data class MatchNode(val id: Int, val eventId: Int, val set: Int, val number: Int, val type: MatchType) :
    Node<Tree<Match>, Match> {
    companion object {
        fun from(matchRow: ResultRow): MatchNode {
            return MatchNode(
                matchRow[MatchTable.id].value,
                matchRow[MatchTable.eventId].value,
                matchRow[MatchTable.set],
                matchRow[MatchTable.number],
                matchRow[MatchTable.type]
            )
        }
    }

    override suspend fun tree(parent: Boolean, excludes: List<Exclude>): MatchTree {
        val eventOrError = if (parent) SQLDatabase.getEventById(eventId) else Success(null)
        val alliancesOrError =
            if (Exclude.MATCH_ALLIANCES in excludes) Success(emptyList()) else SQLDatabase.getAlliancesByMatch(this)

        val event = when (eventOrError) {
            is Success -> eventOrError.value
            is Error -> null
        }

        val alliances = when (alliancesOrError) {
            is Success -> alliancesOrError.value
            is Error -> null
        }

        return MatchTree(this, event, alliances!!)
    }

    override fun leaf(): Match {
        return createMatch(this, emptyList())
    }
}

data class MatchTree(val match: MatchNode, val event: EventNode?, val alliances: List<AllianceNode>) :
    Tree<Match> {

    override suspend fun subtree(): Match {
        val alliances = alliances.map { alliance -> alliance.tree(false, emptyList()).subtree() }

        return createMatch(match, alliances)
    }

    override suspend fun subtree(depth: Int, excludes: List<Exclude>): Match {
        if (depth == 0) return match.leaf()

        val alliances =
            if (Exclude.MATCH_ALLIANCES in excludes) emptyList() else alliances.map { alliance ->
                alliance.tree(false, excludes).subtree(depth - 1, excludes)
            }

        return createMatch(match, alliances)
    }
}

fun createMatch(
    match: MatchNode,
    alliances: List<Alliance>,
): Match {
    return Match(match.set, match.number, match.type, alliances)
}