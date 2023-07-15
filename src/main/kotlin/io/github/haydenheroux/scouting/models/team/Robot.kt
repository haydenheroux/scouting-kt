package io.github.haydenheroux.scouting.models.team

import kotlinx.serialization.Serializable

/**
 * A robot is a robot that is built for FRC.
 *
 * Each robot has properties, many of which are specific to the game that
 * takes place during each season.
 *
 * @property name the name of the robot.
 * @see Season
 */
@Serializable
data class Robot(val name: String) // Add additional properties
