package io.github.haydenheroux.scouting.models.match

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class GameMetric(val key: String, val value: String)

object GameMetrics : IntIdTable() {
    val metric = reference("metric_id", Metrics)
    val key = varchar("key", 255)
    val value = varchar("value", 255)
}
