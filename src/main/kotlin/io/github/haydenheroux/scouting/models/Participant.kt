package io.github.haydenheroux.scouting.models

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Participant(val teamNumber: Int, val metrics: Map<String, String>)

data class ParticipantQuery(val teamQuery: TeamQuery, val allianceQuery: AllianceQuery)

fun participantQueryOf(parameters: Parameters): Result<ParticipantQuery> {
    val teamQuery = teamQueryOf(parameters)

    if (teamQuery.isFailure) {
        return Result.failure(teamQuery.exceptionOrNull()!!)
    }

    val allianceQuery = allianceQueryOf(parameters)

    if (allianceQuery.isFailure) {
        return Result.failure(allianceQuery.exceptionOrNull()!!)
    }

    return Result.success(ParticipantQuery(teamQuery.getOrNull()!!, allianceQuery.getOrNull()!!))
}