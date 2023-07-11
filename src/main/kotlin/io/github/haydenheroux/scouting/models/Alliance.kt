package io.github.haydenheroux.scouting.models

/**
 * An alliance of robots in an FRC match.
 *
 * @property robots the robots on the alliance.
 */
data class Alliance(val robots: MutableList<RobotMetric>)
