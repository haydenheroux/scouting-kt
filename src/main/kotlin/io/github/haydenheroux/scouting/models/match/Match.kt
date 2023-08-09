package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.MatchType
import io.github.haydenheroux.scouting.models.event.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

object MatchTable : IntIdTable() {
    val eventId = reference("eventId", EventTable)
    val number = integer("number")
    val type = enumerationByName<MatchType>("type", 255)
}

@Serializable
data class MatchProperties(
    val number: Int,
    val type: MatchType,
)

fun ResultRow.matchProperties(): MatchProperties {
    val number = this[MatchTable.number]
    val type = this[MatchTable.type]

    return MatchProperties(number, type)
}

data class MatchReference(
    val matchId: Int,
    val eventReference: EventReference,
    val number: Int,
    val type: MatchType,
)

suspend fun ResultRow.matchReference(): MatchReference {
    val matchId = this[MatchTable.id].value
    val properties = this.matchProperties()

    val eventId = this[MatchTable.eventId].value
    val eventReference = db.getEvent(eventId)

    return MatchReference(matchId, eventReference, properties.number, properties.type,)
}

suspend fun MatchReference.dereference(children: Boolean): Match {
    val participants = if (children) db.getParticipants(this).map { it.dereference(true) } else emptyList()
    return Match(number, type, participants)
}

@Serializable
data class Match(
    val number: Int,
    val type: MatchType,
    val participants: List<Participant>
)

data class MatchQuery(val number: Int, val event: EventQuery)

fun Match.query(eventQuery: EventQuery): MatchQuery {
    return MatchQuery(number, eventQuery)
}

fun Parameters.matchQuery(): Result<MatchQuery> {
    val number = this["match"] ?: return Result.failure(Exception("Missing `number` in parameters"))

    val event = this.eventQuery()

    if (event.isFailure) {
        return Result.failure(event.exceptionOrNull()!!)
    }

    return Result.success(MatchQuery(number.toInt(), event.getOrNull()!!))
}
