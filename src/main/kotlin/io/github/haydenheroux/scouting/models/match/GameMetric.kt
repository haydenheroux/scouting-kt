package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.Database.query
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

object GameMetrics : IntIdTable() {
    val metric = reference("metric_id", Metrics)
    val key = varchar("key", 255)
    val value = varchar("value", 255)
}

@Serializable
data class GameMetricData(val key: String, val value: String)

fun ResultRow.asGameMetricData(): GameMetricData {
    val key = this[GameMetrics.key]
    val value = this[GameMetrics.value]

    return GameMetricData(key, value)
}

data class GameMetricReference(val gameMetricData: GameMetricData, val metricReference: MetricReference?)

suspend fun ResultRow.asGameMetricReference(orphan: Boolean): GameMetricReference {
    val gameMetricData = this.asGameMetricData()

    val metricId = this[GameMetrics.metric]
    val metricReference = if (orphan) null else query {
        Metrics.select { Metrics.id eq metricId }.map { it.asMetricReference(false) }.single()
    }

    return GameMetricReference(gameMetricData, metricReference)
}

fun GameMetricReference.dereference(): GameMetric {
    return GameMetric(gameMetricData)
}

@Serializable
data class GameMetric(val gameMetricData: GameMetricData)
