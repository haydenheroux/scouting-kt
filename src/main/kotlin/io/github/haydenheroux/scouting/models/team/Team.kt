package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.models.enums.Region
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * An FRC team.
 *
 * Each FRC team has some properties that identify it. For example, the
 * number, name, and region of the team are all identifiable properties.
 * Each FRC team participates in a number of seasons.
 *
 * @property number the number of the FRC team.
 * @property name the name of the FRC team.
 * @property region the region of the FRC team.
 * @property seasons the seasons that the FRC team has participated in.
 * @see Season
 */
@Serializable
data class Team(val number: Int, val name: String, val region: Region, val seasons: List<Season>)

object Teams : IntIdTable() {
    val number = integer("number")
    val name = varchar("name", 255)
    val region = enumerationByName<Region>("region", 255)
}
