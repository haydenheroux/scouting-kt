package io.github.haydenheroux.scouting.models

import io.github.haydenheroux.scouting.models.enums.Region
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Team(val number: Int, val name: String, val region: Region, val seasons: List<Season>)

data class TeamQuery(val number: Int)

fun teamQueryOf(team: Team): TeamQuery {
    return TeamQuery(team.number)
}

fun Parameters.teamQuery(): Result<TeamQuery> {
    val number = this["team"] ?: return Result.failure(Exception("Missing `team` in parameters"))

    return Result.success(TeamQuery(number.toInt()))
}