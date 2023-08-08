package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.models.enums.Region
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

object Teams : IntIdTable() {
    val number = integer("number")
    val name = varchar("name", 255)
    val region = enumerationByName<Region>("region", 255)
}

@Serializable
data class TeamProperties(val number: Int, val name: String, val region: Region)

fun ResultRow.teamProperties(): TeamProperties {
    val number = this[Teams.number]
    val name = this[Teams.name]
    val region = this[Teams.region]

    return TeamProperties(number, name, region)
}

data class TeamReference(
    val number: Int,
    val name: String,
    val region: Region,
    val seasonReferences: List<SeasonReference>
)

suspend fun ResultRow.asTeamReference(noChildren: Boolean): TeamReference {
    val properties = this.teamProperties()

    val teamId = this[Teams.id]
    val seasonReferences = if (noChildren) listOf() else query {
        Seasons.select { Seasons.team eq teamId }.map { it.asSeasonReference(false, false) }
    }

    return TeamReference(properties.number, properties.name, properties.region, seasonReferences)
}

fun TeamReference.dereference(): Team {
    val seasons = seasonReferences.map { it.dereference() }

    return Team(number, name, region, seasons)
}

@Serializable
data class Team(val number: Int, val name: String, val region: Region, val seasons: List<Season>)
