package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.Database.query
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

object Robots : IntIdTable() {
    val season = reference("season_id", Seasons)
    val name = varchar("name", 255)
}

@Serializable
data class RobotData(val name: String)

fun ResultRow.asRobotData(): RobotData {
    val name = this[Robots.name]

    return RobotData(name)
}

data class RobotReference(val robotData: RobotData, val seasonReference: SeasonReference?)

suspend fun ResultRow.asRobotReference(noParent: Boolean): RobotReference {
    val robotData = this.asRobotData()

    val seasonId = this[Robots.season]
    val seasonReference = if (noParent) null else query {
        Seasons.select { Seasons.id eq seasonId }.map { it.asSeasonReference(false, true) }.single()
    }

    return RobotReference(robotData, seasonReference)
}

fun RobotReference.dereference(): Robot {
    return Robot(robotData)
}

@Serializable
data class Robot(val robotData: RobotData)
