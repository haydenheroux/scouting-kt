package io.github.haydenheroux.scouting.models.team

import io.ktor.http.*
import org.jetbrains.exposed.dao.id.IntIdTable

object RobotTable : IntIdTable() {
    val seasonId = reference("seasonId", SeasonTable)
    val name = varchar("name", 255)
}

data class RobotQuery(val name: String, val season: SeasonQuery)

fun Parameters.robotQuery(): Result<RobotQuery> {
    val name = this["robot"] ?: return Result.failure(Exception("Missing `robot` in parameters"))

    val season = this.seasonQuery()

    if (season.isFailure) {
        return Result.failure(season.exceptionOrNull()!!)
    }

    return Result.success(RobotQuery(name, season.getOrNull()!!))
}
