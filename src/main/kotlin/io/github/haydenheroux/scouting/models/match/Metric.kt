package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.models.team.Robot
import kotlinx.serialization.Serializable

@Serializable
data class Metric(val robot: Robot, val alliance: Alliance, val gameMetrics: List<GameMetric>)
