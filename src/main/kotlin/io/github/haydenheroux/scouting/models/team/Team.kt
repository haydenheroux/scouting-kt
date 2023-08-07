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
data class TeamData(val number: Int, val name: String, val region: Region)

fun ResultRow.asTeamData(): TeamData {
    val number = this[Teams.number]
    val name = this[Teams.name]
    val region = this[Teams.region]

    return TeamData(number, name, region)
}

data class TeamReference(val teamData: TeamData, val seasonReferences: List<SeasonReference>)

suspend fun ResultRow.asTeamReference(): TeamReference {
    val teamData = this.asTeamData()

    val teamId = this[Teams.id]
    val seasonReferences = query {
        Seasons.select { Seasons.team eq teamId }.map { it.asSeasonReference(true) }
    }

    return TeamReference(teamData, seasonReferences)
}

fun TeamReference.dereference(): Team {
    val seasons = seasonReferences.map { it.dereference() }
    return Team(teamData, seasons)
}

@Serializable
data class Team(val teamData: TeamData, val seasons: List<Season>)
