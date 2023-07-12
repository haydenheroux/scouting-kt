package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.models.team.Robot
import kotlinx.serialization.Serializable

@Serializable
data class RobotMetric(val robot: Robot, val gameMetrics: MutableList<GameSpecificMetric>)
