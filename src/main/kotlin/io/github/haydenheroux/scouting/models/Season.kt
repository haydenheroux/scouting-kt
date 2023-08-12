package io.github.haydenheroux.scouting.models

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Season(val year: Int, val robots: List<Robot>, val events: List<Event>)

data class SeasonQuery(val year: Int, val teamQuery: TeamQuery)

fun seasonQueryOf(season: Season, team: Team): SeasonQuery {
    val teamQuery = teamQueryOf(team)

    return SeasonQuery(season.year, teamQuery)
}

fun seasonQueryOf(parameters: Parameters): Result<SeasonQuery> {
    val year = parameters["year"] ?: return Result.failure(Exception("Missing `year` in parameters"))

    val teamQuery = teamQueryOf(parameters)

    if (teamQuery.isFailure) {
        return Result.failure(teamQuery.exceptionOrNull()!!)
    }

    return Result.success(SeasonQuery(year.toInt(), teamQuery.getOrNull()!!))
}