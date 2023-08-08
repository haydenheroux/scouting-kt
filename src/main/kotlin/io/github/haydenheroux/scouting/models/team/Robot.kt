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
data class RobotProperties(val name: String)

fun ResultRow.robotProperties(): RobotProperties {
    val name = this[Robots.name]

    return RobotProperties(name)
}

data class RobotReference(val name: String, val seasonReference: SeasonReference?)

suspend fun ResultRow.asRobotReference(noParent: Boolean): RobotReference {
    val properties = this.robotProperties()

    val seasonId = this[Robots.season]
    val seasonReference = if (noParent) null else query {
        Seasons.select { Seasons.id eq seasonId }.map { it.asSeasonReference(false, true) }.single()
    }

    return RobotReference(properties.name, seasonReference)
}

fun RobotReference.dereference(): Robot {
    return Robot(name)
}

@Serializable
data class Robot(val name: String)
