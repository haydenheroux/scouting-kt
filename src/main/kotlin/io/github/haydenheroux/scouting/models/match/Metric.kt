package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.models.enums.Alliance
import io.github.haydenheroux.scouting.models.team.Robot
import io.github.haydenheroux.scouting.models.team.Robots
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Metric(val robot: Robot, val alliance: Alliance, val gameMetrics: List<GameMetric>)

object Metrics : IntIdTable() {
    val match = reference("match_id", Matches)
    val robot = reference("robot_id", Robots)
    val alliance = enumerationByName<Alliance>("alliance", 255)
}
