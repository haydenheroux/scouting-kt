package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.models.enums.MatchType
import io.github.haydenheroux.scouting.models.event.EventQuery
import io.github.haydenheroux.scouting.models.event.EventTable
import io.github.haydenheroux.scouting.models.event.eventQuery
import io.ktor.http.*
import org.jetbrains.exposed.dao.id.IntIdTable

object MatchTable : IntIdTable() {
    val eventId = reference("eventId", EventTable)
    val number = integer("number")
    val type = enumerationByName<MatchType>("type", 255)
}

data class MatchQuery(val number: Int, val event: EventQuery)

fun Parameters.matchQuery(): Result<MatchQuery> {
    val number = this["match"] ?: return Result.failure(Exception("Missing `number` in parameters"))

    val event = this.eventQuery()

    if (event.isFailure) {
        return Result.failure(event.exceptionOrNull()!!)
    }

    return Result.success(MatchQuery(number.toInt(), event.getOrNull()!!))
}
