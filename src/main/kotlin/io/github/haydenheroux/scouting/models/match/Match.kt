package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.MatchType
import io.github.haydenheroux.scouting.models.event.EventNode
import io.github.haydenheroux.scouting.models.event.EventQuery
import io.github.haydenheroux.scouting.models.event.EventTable
import io.github.haydenheroux.scouting.models.event.eventQuery
import io.github.haydenheroux.scouting.models.interfaces.Branch
import io.github.haydenheroux.scouting.models.interfaces.Node
import io.github.haydenheroux.scouting.models.interfaces.Parent
import io.github.haydenheroux.scouting.models.interfaces.Tree
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object MatchTable : IntIdTable() {
    val eventId = reference("eventId", EventTable)
    val number = integer("number")
    val type = enumerationByName<MatchType>("type", 255)
}

data class MatchNode(val id: Int, val number: Int, val type: MatchType) : Node<Tree<Match>, Match> {

    companion object {
        fun from(matchRow: ResultRow): MatchNode {
            return MatchNode(
                matchRow[MatchTable.id].value,
                matchRow[MatchTable.number],
                matchRow[MatchTable.type]
            )
        }
    }

    override suspend fun parent(): MatchParent {
        val event = db.getEventByMatch(this).getOrNull()!!

        return MatchParent(this, event)
    }

    override suspend fun branch(): Branch<Tree<Match>, Match> {
        val participants = db.getParticipantsByMatch(this).getOrNull()!!

        return MatchBranch(this, participants)
    }

    override fun tree(): Tree<Match> {
        return MatchTree(this, emptyList())
    }
}

data class MatchParent(val match: MatchNode, val event: EventNode) : Parent<Tree<Match>, Match> {
    override suspend fun branch(): Branch<Tree<Match>, Match> {
        return match.branch()
    }

    override fun tree(): Tree<Match> {
        return match.tree()
    }
}

data class MatchBranch(val match: MatchNode, val participants: List<ParticipantNode>) : Branch<Tree<Match>, Match> {
    override suspend fun parent(): Parent<Tree<Match>, Match> {
        return match.parent()
    }

    override suspend fun tree(): Tree<Match> {
        val participants = participants.map { it.branch() }

        return MatchTree(match, participants)
    }
}

data class MatchTree(val match: MatchNode, val participants: List<Branch<Tree<Participant>, Participant>>) :
    Tree<Match> {
    override fun leaf(): Match {
        return Match(match.number, match.type, emptyList())
    }

    override suspend fun leaves(): Match {
        val participants = participants.map { participant -> participant.tree().leaf() }

        return Match(match.number, match.type, participants)
    }

    override suspend fun subtree(): Match {
        val participants = participants.map { participant -> participant.tree().subtree() }

        return Match(match.number, match.type, participants)
    }

    override suspend fun subtree(depth: Int): Match {
        if (depth == 0) return leaf()
        if (depth == 1) return leaves()

        val participants = participants.map { participant -> participant.tree().subtree(depth - 1) }

        return Match(match.number, match.type, participants)
    }
}

@Serializable
data class Match(val number: Int, val type: MatchType, val participants: List<Participant>)

data class MatchQuery(val number: Int, val event: EventQuery)

fun matchQueryOf(match: Match, eventQuery: EventQuery): MatchQuery {
    return MatchQuery(match.number, eventQuery)
}

fun Parameters.matchQuery(): Result<MatchQuery> {
    val number = this["match"] ?: return Result.failure(Exception("Missing `number` in parameters"))

    val event = this.eventQuery()

    if (event.isFailure) {
        return Result.failure(event.exceptionOrNull()!!)
    }

    return Result.success(MatchQuery(number.toInt(), event.getOrNull()!!))
}
