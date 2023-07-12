package io.github.haydenheroux.scouting.models.team

import kotlinx.serialization.Serializable

/**
 * An FRC team.
 *
 * Each FRC team has some properties that identify it. For example, the
 * number, name, and location of the team are all identifiable properties.
 * Each FRC team participates in a number of seasons.
 *
 * @property number the number of the FRC team.
 * @property name the name of the FRC team.
 * @property location the location of the FRC team.
 * @property seasons the seasons that the FRC team has participated in.
 * @see Season
 */
@Serializable
data class Team(val number: Int, val name: String, val location: String, val seasons: MutableMap<Int, Season>)
