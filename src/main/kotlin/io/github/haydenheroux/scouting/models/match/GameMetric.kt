package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.Database.query
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

object GameMetricTable : IntIdTable() {
    val participantId = reference("participantId", ParticipantTable)
    val key = varchar("key", 255)
    val value = varchar("value", 255)
}

@Serializable
data class GameMetricProperties(val key: String, val value: String)

fun ResultRow.gameMetricProperties(): GameMetricProperties {
    val key = this[GameMetricTable.key]
    val value = this[GameMetricTable.value]

    return GameMetricProperties(key, value)
}

data class GameMetricReference(val key: String, val value: String, val participantReference: ParticipantReference?)

suspend fun ResultRow.asGameMetricReference(noParent: Boolean): GameMetricReference {
    val properties = this.gameMetricProperties()

    val participantId = this[GameMetricTable.participantId]
    val participantReference = if (noParent) null else query {
        ParticipantTable.select { ParticipantTable.id eq participantId }.map { it.asParticipantReference(false, true) }
            .single()
    }

    return GameMetricReference(properties.key, properties.value, participantReference)
}

fun GameMetricReference.dereference(): GameMetric {
    return GameMetric(key, value)
}

@Serializable
data class GameMetric(val key: String, val value: String)
