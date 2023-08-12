package io.github.haydenheroux.scouting.models

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Robot(val name: String)

data class RobotQuery(val name: String, val seasonQuery: SeasonQuery)

fun robotQueryOf(robot: Robot, season: Season, team: Team): RobotQuery {
    val seasonQuery = seasonQueryOf(season, team)

    return RobotQuery(robot.name, seasonQuery)
}

fun robotQueryOf(parameters: Parameters): Result<RobotQuery> {
    val name = parameters["robot"] ?: return Result.failure(Exception("Missing `robot` in parameters"))

    val seasonQuery = seasonQueryOf(parameters)

    if (seasonQuery.isFailure) {
        return Result.failure(seasonQuery.exceptionOrNull()!!)
    }

    return Result.success(RobotQuery(name, seasonQuery.getOrNull()!!))
}