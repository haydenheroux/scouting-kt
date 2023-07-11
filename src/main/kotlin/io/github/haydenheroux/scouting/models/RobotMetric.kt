package io.github.haydenheroux.scouting.models

data class RobotMetric(val robot: Robot, val gameMetrics: MutableList<GameSpecificMetric>)
