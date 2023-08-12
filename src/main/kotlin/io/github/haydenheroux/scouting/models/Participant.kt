package io.github.haydenheroux.scouting.models

import io.github.haydenheroux.scouting.models.enums.Alliance
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Participant(val alliance: Alliance, @Transient val team: Team? = null, val metrics: List<Metric>)
data class ParticipantQuery(val team: TeamQuery, val match: MatchQuery)

fun Parameters.participantQuery(): Result<ParticipantQuery> {
    val team = this.teamQuery()

    if (team.isFailure) {
        return Result.failure(team.exceptionOrNull()!!)
    }

    val match = this.matchQuery()

    if (match.isFailure) {
        return Result.failure(match.exceptionOrNull()!!)
    }

    return Result.success(ParticipantQuery(team.getOrNull()!!, match.getOrNull()!!))
}