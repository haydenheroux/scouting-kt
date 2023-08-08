package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.Database.query
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
    val number: Int,
    val type: MatchType,
    val eventReference: EventReference?,
    val metricReferences: List<MetricReference>
)

suspend fun ResultRow.asMatchReference(noParent: Boolean, noChildren: Boolean): MatchReference {
    val properties = this.matchProperties()

    val eventId = this[MatchTable.eventId]
    val eventReference = if (noParent) null else query {
        EventTable.select { EventTable.id eq eventId }.map { it.asEventReference(true) }.single()
    }

    val matchId = this[MatchTable.id]
    val metricReferences = if (noChildren) listOf() else query {
        MetricTable.select { MetricTable.matchId eq matchId }.map { it.asMetricReference(false, false) }
    }

    return MatchReference(properties.number, properties.type, eventReference, metricReferences)
}

fun MatchReference.dereference(): Match {
    val metrics = metricReferences.map { it.dereference() }
    return Match(number, type, metrics)
}

@Serializable
data class Match(
    val number: Int,
    val type: MatchType,
    val metrics: List<Metric>
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
