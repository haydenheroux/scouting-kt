package io.github.haydenheroux.scouting.models

import io.github.haydenheroux.scouting.models.enums.Alliance
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Participant(val alliance: Alliance, @Transient val team: Team? = null, val metrics: List<Metric>)

data class ParticipantQuery(val team: TeamQuery, val match: MatchQuery)

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