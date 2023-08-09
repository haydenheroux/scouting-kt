package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.Database.query
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

object MetricTable : IntIdTable() {
    val participantId = reference("participantId", ParticipantTable)
    val key = varchar("key", 255)
    val value = varchar("value", 255)
}

@Serializable
data class MetricProperties(val key: String, val value: String)

fun ResultRow.metricProperties(): MetricProperties {
    val key = this[MetricTable.key]
    val value = this[MetricTable.value]

    return MetricProperties(key, value)
}

data class MetricReference(val key: String, val value: String, val participantReference: ParticipantReference?)

suspend fun ResultRow.metricReference(noParent: Boolean): MetricReference {
    val properties = this.metricProperties()

    val participantId = this[MetricTable.participantId]
    val participantReference = if (noParent) null else query {
        ParticipantTable.select { ParticipantTable.id eq participantId }.map { it.asParticipantReference(false, true) }
            .single()
    }

    return MetricReference(properties.key, properties.value, participantReference)
}

fun MetricReference.dereference(): Metric {
    return Metric(key, value)
}

@Serializable
data class Metric(val key: String, val value: String)
