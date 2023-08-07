package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.models.enums.Alliance
import io.github.haydenheroux.scouting.models.team.RobotReference
import io.github.haydenheroux.scouting.models.team.Robots
import io.github.haydenheroux.scouting.models.team.asRobotReference
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

object Metrics : IntIdTable() {
    val match = reference("match_id", Matches)
    val robot = reference("robot_id", Robots)
    val alliance = enumerationByName<Alliance>("alliance", 255)
}

@Serializable
data class MetricData(
    val alliance: Alliance,
)

fun ResultRow.asMetricData(): MetricData {
    val alliance = this[Metrics.alliance]

    return MetricData(alliance)
}

data class MetricReference(
    val metricData: MetricData,
    val matchReference: MatchReference?,
    val robotReference: RobotReference?,
    val gameMetricReferences: List<GameMetricReference>
)

suspend fun ResultRow.asMetricReference(orphan: Boolean): MetricReference {
    val metricData = this.asMetricData()

    val matchId = this[Metrics.match]
    val matchReference = if (orphan) null else query {
        Matches.select { Matches.id eq matchId }.map { it.asMatchReference(false) }.single()
    }
    val robotId = this[Metrics.robot]
    val robotReference = if (orphan) null else query {
        Robots.select { Robots.id eq robotId }.map { it.asRobotReference(false) }.single()
    }

    val metricId = this[Metrics.id]
    val gameMetricReferences = query {
        GameMetrics.select { GameMetrics.metric eq metricId }.map { it.asGameMetricReference(orphan) }
    }

    return MetricReference(metricData, matchReference, robotReference, gameMetricReferences)
}

fun MetricReference.dereference(): Metric {
    val gameMetrics = gameMetricReferences.map { it.dereference() }
    return Metric(metricData, gameMetrics)
}

@Serializable
data class Metric(
    val metricData: MetricData,
    val gameMetrics: List<GameMetric>
)
