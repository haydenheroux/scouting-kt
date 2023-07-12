package io.github.haydenheroux.scouting.models.match

import kotlinx.serialization.Serializable

/**
 * An alliance of robots in an FRC match.
 *
 * @property color the color of the alliance.
 * @property robots the robots on the alliance.
 */
@Serializable
data class Alliance(val color: AllianceColor, val robots: MutableList<RobotMetric>)
