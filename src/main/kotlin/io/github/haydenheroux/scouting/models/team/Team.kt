package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.models.enums.Region
import io.ktor.http.*
import org.jetbrains.exposed.dao.id.IntIdTable

object TeamTable : IntIdTable() {
    val number = integer("number")
    val name = varchar("name", 255)
    val region = enumerationByName<Region>("region", 255)
}

data class TeamQuery(val number: Int)

fun Parameters.teamQuery(): Result<TeamQuery> {
    val number = this["team"] ?: return Result.failure(Exception("Missing `team` in parameters"))

    return Result.success(TeamQuery(number.toInt()))
}
