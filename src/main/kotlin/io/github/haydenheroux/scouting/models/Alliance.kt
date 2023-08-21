package io.github.haydenheroux.scouting.models

import io.github.haydenheroux.scouting.models.enums.AllianceColor
import io.github.haydenheroux.scouting.models.enums.allianceColorOf
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Alliance(
    val color: AllianceColor,
    val metrics: List<Metric>,
    val participants: List<Participant>
)

data class AllianceQuery(
    val matchQuery: MatchQuery,
    val color: AllianceColor
)

fun allianceQueryOf(alliance: Alliance, match: Match, event: Event): AllianceQuery {
    val matchQuery = matchQueryOf(match, event)

    return AllianceQuery(matchQuery, alliance.color)
}

fun allianceQueryOf(parameters: Parameters): Result<AllianceQuery> {
    val matchQueryResult = matchQueryOf(parameters)

    matchQueryResult.getOrNull()?.let { matchQuery ->
        val colorParameter =
            parameters["alliance"] ?: return Result.failure(Exception("Missing `alliance` in parameters"))

        // TODO
        val color = allianceColorOf[colorParameter]!!

        return Result.success(AllianceQuery(matchQuery, color))
    } ?: run {
        return Result.failure(matchQueryResult.exceptionOrNull()!!)
    }
}