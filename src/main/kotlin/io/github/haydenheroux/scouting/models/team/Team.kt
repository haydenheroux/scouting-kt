package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.models.enums.Region
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

object TeamTable : IntIdTable() {
    val number = integer("number")
    val name = varchar("name", 255)
    val region = enumerationByName<Region>("region", 255)
}

@Serializable
data class TeamProperties(val number: Int, val name: String, val region: Region)

fun ResultRow.teamProperties(): TeamProperties {
    val number = this[TeamTable.number]
    val name = this[TeamTable.name]
    val region = this[TeamTable.region]

    return TeamProperties(number, name, region)
}

data class TeamReference(
    val number: Int,
    val name: String,
    val region: Region,
    val seasonReferences: List<SeasonReference>
)

suspend fun ResultRow.teamReference(noChildren: Boolean): TeamReference {
    val properties = this.teamProperties()

    val teamId = this[TeamTable.id]
    val seasonReferences = if (noChildren) listOf() else query {
        SeasonTable.select { SeasonTable.teamId eq teamId }.map { it.seasonReference(false, false) }
    }

    return TeamReference(properties.number, properties.name, properties.region, seasonReferences)
}

fun TeamReference.dereference(): Team {
    val seasons = seasonReferences.map { it.dereference() }

    return Team(number, name, region, seasons)
}

@Serializable
data class Team(val number: Int, val name: String, val region: Region, val seasons: List<Season>)

data class TeamQuery(val number: Int)

fun Team.query(): TeamQuery {
    return TeamQuery(number)
}

fun Parameters.teamQuery(): Result<TeamQuery> {
    val number = this["team"] ?: return Result.failure(Exception("Missing `team` in parameters"))

    return Result.success(TeamQuery(number.toInt()))
}
