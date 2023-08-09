package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.database.db
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

data class RobotReference(val robotId: Int, val seasonReference: SeasonReference, val name: String)

suspend fun ResultRow.robotReference(): RobotReference {
    val robotId = this[RobotTable.id].value
    val properties = this.robotProperties()

    val seasonId = this[RobotTable.seasonId].value
    val seasonReference = db.getSeason(seasonId)

    return RobotReference(robotId, seasonReference, properties.name)
}

fun RobotReference.dereference(children: Boolean): Robot {
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
