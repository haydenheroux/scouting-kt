package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.models.enums.Alliance
import io.github.haydenheroux.scouting.models.team.Robot
import io.github.haydenheroux.scouting.models.team.Robots
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Metric(
    @Transient var match: Match? = null,
    @Transient var robot: Robot? = null,
    val alliance: Alliance,
    val gameMetrics: List<GameMetric>
)

object Metrics : IntIdTable() {
    val match = reference("match_id", Matches)
    val robot = reference("robot_id", Robots)
    val alliance = enumerationByName<Alliance>("alliance", 255)
}
