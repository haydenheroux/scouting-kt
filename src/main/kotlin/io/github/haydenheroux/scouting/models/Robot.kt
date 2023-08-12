package io.github.haydenheroux.scouting.models

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Robot(val name: String)
data class RobotQuery(val name: String, val season: SeasonQuery)

fun robotQueryOf(robot: Robot, seasonQuery: SeasonQuery): RobotQuery {
    return RobotQuery(robot.name, seasonQuery)
}

fun Parameters.robotQuery(): Result<RobotQuery> {
    val name = this["robot"] ?: return Result.failure(Exception("Missing `robot` in parameters"))

    val season = this.seasonQuery()

    if (season.isFailure) {
        return Result.failure(season.exceptionOrNull()!!)
    }

    return Result.success(RobotQuery(name, season.getOrNull()!!))
}