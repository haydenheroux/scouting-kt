package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.database.db
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

data class MetricReference(val metricId: Int, val participantReference: ParticipantReference, val key: String, val value: String)

suspend fun ResultRow.metricReference(): MetricReference {
    val metricId = this[MetricTable.id].value
    val properties = this.metricProperties()

    val participantId = this[MetricTable.participantId].value
    val participantReference = db.getParticipant(participantId)

    return MetricReference(metricId, participantReference, properties.key, properties.value)
}

fun MetricReference.dereference(children: Boolean): Metric {
    return Metric(key, value)
}

@Serializable
data class Metric(val key: String, val value: String)
