package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.Region
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

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

suspend fun ResultRow.toTeam(): Team {
    val teamId: Int = this[Teams.id].value

    val number = this[Teams.number]
    val name = this[Teams.name]
    val region = this[Teams.region]
    val seasons = db.findSeasons(teamId)

    return Team(number, name, region, seasons)
}
