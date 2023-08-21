package io.github.haydenheroux.scouting.models

import io.github.haydenheroux.scouting.models.enums.AllianceColor
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Participant(val allianceColor: AllianceColor, val teamNumber: Int, val metrics: List<Metric>)

data class ParticipantQuery(val teamQuery: TeamQuery, val matchQuery: MatchQuery)

fun participantQueryOf(parameters: Parameters): Result<ParticipantQuery> {
    val teamQuery = teamQueryOf(parameters)

    if (teamQuery.isFailure) {
        return Result.failure(teamQuery.exceptionOrNull()!!)
    }

    val matchQuery = matchQueryOf(parameters)

    if (matchQuery.isFailure) {
        return Result.failure(matchQuery.exceptionOrNull()!!)
    }

    return Result.success(ParticipantQuery(teamQuery.getOrNull()!!, matchQuery.getOrNull()!!))
}