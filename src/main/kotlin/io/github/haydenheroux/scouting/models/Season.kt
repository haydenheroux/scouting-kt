package io.github.haydenheroux.scouting.models

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Season(val year: Int, val robots: List<Robot>, val events: List<Event>)
data class SeasonQuery(val year: Int, val team: TeamQuery)

fun seasonQueryOf(season: Season, teamQuery: TeamQuery): SeasonQuery {
    return SeasonQuery(season.year, teamQuery)
}

fun Parameters.seasonQuery(): Result<SeasonQuery> {
    val year = this["year"] ?: return Result.failure(Exception("Missing `year` in parameters"))

    val team = this.teamQuery()

    if (team.isFailure) {
        return Result.failure(team.exceptionOrNull()!!)
    }

    return Result.success(SeasonQuery(year.toInt(), team.getOrNull()!!))
}