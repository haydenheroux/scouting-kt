package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.Database.query
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

object GameMetricTable : IntIdTable() {
    val metricId = reference("metricId", MetricTable)
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

data class GameMetricReference(val key: String, val value: String, val metricReference: MetricReference?)

suspend fun ResultRow.asGameMetricReference(noParent: Boolean): GameMetricReference {
    val properties = this.gameMetricProperties()

    val metricId = this[GameMetricTable.metricId]
    val metricReference = if (noParent) null else query {
        MetricTable.select { MetricTable.id eq metricId }.map { it.asMetricReference(false, true) }.single()
    }

    return GameMetricReference(properties.key, properties.value, metricReference)
}

fun GameMetricReference.dereference(): GameMetric {
    return GameMetric(key, value)
}

@Serializable
data class GameMetric(val key: String, val value: String)
