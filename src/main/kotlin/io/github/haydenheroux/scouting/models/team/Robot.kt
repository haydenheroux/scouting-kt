package io.github.haydenheroux.scouting.models.team

/**
 * A robot is a robot that is built for FRC.
 *
 * Each robot is built in and competes in a season. Each robot may have
 * other properties, many of which are specific to the game that takes
 * place during each season.
 *
 * @property season the season that the robot was built in and competed in.
 * @property name the name of the robot.
 * @see Season
 */
data class Robot(val season: Season, val name: String) // Add additional properties
