package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.tree.Branch
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
    Node<Branch<Match>, Match> {
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

    override suspend fun tree(): MatchTree {
        val event = SQLDatabase.getEventById(eventId).getOrNull()!!
        val participants = SQLDatabase.getParticipantsByMatch(this).getOrNull()!!

        return MatchTree(this, event, participants)
    }

    override fun root(): Branch<Match> {
        return MatchBranch(this, emptyList())
    }
}

data class MatchTree(val match: MatchNode, val event: EventNode, val participants: List<ParticipantNode>) :
    Tree<Branch<Match>, Match> {
    override suspend fun branch(): Branch<Match> {
        val participants = participants.map { it.tree() }

        return MatchBranch(match, participants)
    }
}

data class MatchBranch(val match: MatchNode, val participants: List<Tree<Branch<Participant>, Participant>>) :
    Branch<Match> {
    override fun leaf(): Match {
        return Match(match.set, match.number, match.type, emptyList())
    }

    override suspend fun leaves(): Match {
        val participants = participants.map { participant -> participant.branch().leaf() }

        return Match(match.set, match.number, match.type, participants)
    }

    override suspend fun subbranch(): Match {
        val participants = participants.map { participant -> participant.branch().subbranch() }

        return Match(match.set, match.number, match.type, participants)
    }

    override suspend fun subbranch(depth: Int): Match {
        if (depth == 0) return leaf()
        if (depth == 1) return leaves()

        val participants = participants.map { participant -> participant.branch().subbranch(depth - 1) }

        return Match(match.set, match.number, match.type, participants)
    }
}

