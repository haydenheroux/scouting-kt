package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.models.enums.Alliance
import io.github.haydenheroux.scouting.models.team.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

object MetricTable : IntIdTable() {
    val matchId = reference("matchId", MatchTable)
    val robotId = reference("robotId", RobotTable)
    val alliance = enumerationByName<Alliance>("alliance", 255)
}

@Serializable
data class MetricProperties(
    val alliance: Alliance,
)

fun ResultRow.metricProperties(): MetricProperties {
    val alliance = this[MetricTable.alliance]

    return MetricProperties(alliance)
}

data class MetricReference(
    val alliance: Alliance,
    val matchReference: MatchReference?,
    val robotReference: RobotReference?,
    val gameMetricReferences: List<GameMetricReference>
)

suspend fun ResultRow.asMetricReference(noParent: Boolean, noChildren: Boolean): MetricReference {
    val properties = this.metricProperties()

    val matchId = this[MetricTable.matchId]
    val matchReference = if (noParent) null else query {
        MatchTable.select { MatchTable.id eq matchId }.map { it.asMatchReference(false, true) }.single()
    }
    val robotId = this[MetricTable.robotId]
    val robotReference = if (noParent) null else query {
        RobotTable.select { RobotTable.id eq robotId }.map { it.asRobotReference(false) }.single()
    }

    val metricId = this[MetricTable.id]
    val gameMetricReferences = if (noChildren) listOf() else query {
        GameMetricTable.select { GameMetricTable.metricId eq metricId }.map { it.asGameMetricReference(false) }
    }

    return MetricReference(properties.alliance, matchReference, robotReference, gameMetricReferences)
}

fun MetricReference.dereference(): Metric {
    // TODO Handle robotReference == null
    val robot = robotReference!!.dereference()
    val gameMetrics = gameMetricReferences.map { it.dereference() }
    return Metric(robot, alliance, gameMetrics)
}

@Serializable
data class Metric(
    val robot: Robot,
    val alliance: Alliance,
    val gameMetrics: List<GameMetric>
)

data class MetricQuery(val match: MatchQuery, val robot: RobotQuery)

fun Parameters.metricQuery(): MetricQuery {
    val match = this.matchQuery()
    val robot = this.robotQuery()

    return MetricQuery(match, robot)
}
