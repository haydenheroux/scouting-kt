package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.Region
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

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
    val teamId: Int,
    val number: Int,
    val name: String,
    val region: Region,
)

fun ResultRow.teamReference(): TeamReference {
    val teamId = this[TeamTable.id].value
    val properties = this.teamProperties()

    return TeamReference(teamId, properties.number, properties.name, properties.region)
}

suspend fun TeamReference.dereference(children: Boolean): Team {
    val seasons = if (children) db.getSeasons(this).map { it.dereference(true) } else emptyList()

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
