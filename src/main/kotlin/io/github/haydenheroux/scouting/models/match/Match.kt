package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.models.enums.MatchType
import io.github.haydenheroux.scouting.models.event.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

object Matches : IntIdTable() {
    val event = reference("event_id", Events)
    val number = integer("number")
    val type = enumerationByName<MatchType>("type", 255)
}

@Serializable
data class MatchProperties(
    val number: Int,
    val type: MatchType,
)

fun ResultRow.matchProperties(): MatchProperties {
    val number = this[Matches.number]
    val type = this[Matches.type]

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

    val eventId = this[Matches.event]
    val eventReference = if (noParent) null else query {
        Events.select { Events.id eq eventId }.map { it.asEventReference(true) }.single()
    }

    val matchId = this[Matches.id]
    val metricReferences = if (noChildren) listOf() else query {
        Metrics.select { Metrics.match eq matchId }.map { it.asMetricReference(false, false) }
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

fun Parameters.matchQuery(): MatchQuery {
    val number = this["match"]!!.toInt()

    val event = this.eventQuery()

    return MatchQuery(number, event)
}
