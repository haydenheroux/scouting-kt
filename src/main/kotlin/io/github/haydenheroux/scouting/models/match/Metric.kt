package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.models.team.Robot
import kotlinx.serialization.Serializable

@Serializable
data class Metric(val robot: Robot, val alliance: AllianceColor, val gameMetrics: List<GameMetric>)
