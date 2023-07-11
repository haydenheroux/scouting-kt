package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.models.team.Robot

data class RobotMetric(val robot: Robot, val gameMetrics: MutableList<GameSpecificMetric>)
