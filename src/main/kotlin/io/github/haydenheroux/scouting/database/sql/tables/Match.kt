package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.models.Match
import io.github.haydenheroux.scouting.models.Participant
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

    override suspend fun tree(parent: Boolean): MatchTree {
        val event = if (parent) SQLDatabase.getEventById(eventId).getOrNull()!! else null
        val participants = SQLDatabase.getParticipantsByMatch(this).getOrNull()!!

        return MatchTree(this, event, participants)
    }

    override fun leaf(): Match {
        return createMatch(this, emptyList())
    }
}

data class MatchTree(val match: MatchNode, val event: EventNode?, val participants: List<ParticipantNode>) :
    Tree<Match> {
    override suspend fun leaves(): Match {
        val participants = participants.map { participant -> participant.leaf() }

        return createMatch(match, participants)
    }

    override suspend fun subtree(): Match {
        val participants = participants.map { participant -> participant.tree(false).subtree() }

        return createMatch(match, participants)
    }

    override suspend fun subtree(depth: Int): Match {
        if (depth == 0) return match.leaf()
        if (depth == 1) return leaves()

        val participants = participants.map { participant -> participant.tree(false).subtree(depth - 1) }

        return createMatch(match, participants)
    }
}

fun createMatch(match: MatchNode, participants: List<Participant>): Match {
    return Match(match.set, match.number, match.type, participants)
}