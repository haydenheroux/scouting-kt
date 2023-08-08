package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.Database.query
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

object RobotTable : IntIdTable() {
    val seasonId = reference("seasonId", SeasonTable)
    val name = varchar("name", 255)
}

@Serializable
data class RobotProperties(val name: String)

fun ResultRow.robotProperties(): RobotProperties {
    val name = this[RobotTable.name]

    return RobotProperties(name)
}

data class RobotReference(val name: String, val seasonReference: SeasonReference?)

suspend fun ResultRow.asRobotReference(noParent: Boolean): RobotReference {
    val properties = this.robotProperties()

    val seasonId = this[RobotTable.seasonId]
    val seasonReference = if (noParent) null else query {
        SeasonTable.select { SeasonTable.id eq seasonId }.map { it.asSeasonReference(false, true) }.single()
    }

    return RobotReference(properties.name, seasonReference)
}

fun RobotReference.dereference(): Robot {
    return Robot(name)
}

@Serializable
data class Robot(val name: String)

data class RobotQuery(val name: String, val season: SeasonQuery)

fun Robot.query(seasonQuery: SeasonQuery): RobotQuery {
    return RobotQuery(name, seasonQuery)
}

fun Parameters.robotQuery(): Result<RobotQuery> {
    val name = this["robot"] ?: return Result.failure(Exception("Missing `robot` in parameters"))

    val season = this.seasonQuery()

    if (season.isFailure) {
        return Result.failure(season.exceptionOrNull()!!)
    }

    return Result.success(RobotQuery(name, season.getOrNull()!!))
}
