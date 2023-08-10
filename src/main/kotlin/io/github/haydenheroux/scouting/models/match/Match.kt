package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.MatchType
import io.github.haydenheroux.scouting.models.event.EventNode
import io.github.haydenheroux.scouting.models.event.EventQuery
import io.github.haydenheroux.scouting.models.event.EventTable
import io.github.haydenheroux.scouting.models.event.eventQuery
import io.github.haydenheroux.scouting.models.interfaces.Node
import io.github.haydenheroux.scouting.models.interfaces.Parent
import io.github.haydenheroux.scouting.models.interfaces.Subtree
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object MatchTable : IntIdTable() {
    val eventId = reference("eventId", EventTable)
    val number = integer("number")
    val type = enumerationByName<MatchType>("type", 255)
}

data class MatchNode(val id: Int, val number: Int, val type: MatchType) : Node<MatchTree> {

    companion object {
        fun from(matchRow: ResultRow): MatchNode {
            return MatchNode(
                matchRow[MatchTable.id].value,
                matchRow[MatchTable.number],
                matchRow[MatchTable.type]
            )
        }
    }

    override suspend fun parent(): Parent<MatchTree> {
        val event = db.getEventByMatch(this)

        return MatchParent(this, event)
    }

    override suspend fun subtree(): Subtree<MatchTree> {
        val participants = db.getParticipantsByMatch(this)

        return MatchSubtree(this, participants)
    }

    override fun tree(): MatchTree {
        return MatchTree(this, emptyList())
    }
}

data class MatchParent(val match: MatchNode, val event: EventNode) : Parent<MatchTree> {
    override suspend fun subtree(): Subtree<MatchTree> {
        return match.subtree()
    }

    override fun tree(): MatchTree {
        return match.tree()
    }
}

data class MatchSubtree(val match: MatchNode, val participants: List<ParticipantNode>) : Subtree<MatchTree> {
    override suspend fun parent(): Parent<MatchTree> {
        return match.parent()
    }

    override suspend fun tree(): MatchTree {
        val participants = participants.map { it.subtree() }

        return MatchTree(match, participants)
    }
}

data class MatchTree(val match: MatchNode, val participants: List<Subtree<ParticipantTree>>) {
    fun noChildren(): Match {
        return Match(match.number, match.type, emptyList())
    }

    suspend fun children(): Match {
        val participants =
            participants.map { participant -> participant.tree().noChildren() }

        return Match(match.number, match.type, participants)
    }

    suspend fun subChildren(): Match {
        val participants =
            participants.map { participant -> participant.tree().subChildren() }

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
