package io.github.haydenheroux.scouting.models

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Metric(val key: String, val value: String)

data class ParticipantMetricQuery(val key: String, val participantQuery: ParticipantQuery)

fun participantMetricQueryOf(
    metric: Metric,
    participant: Participant,
    match: Match,
    event: Event
): ParticipantMetricQuery {
    val teamQuery = TeamQuery(participant.teamNumber)
    val matchQuery = matchQueryOf(match, event)
    val participantQuery = ParticipantQuery(teamQuery, matchQuery)

    return ParticipantMetricQuery(metric.key, participantQuery)
}

fun participantMetricQueryOf(parameters: Parameters): Result<ParticipantMetricQuery> {
    val key = parameters["key"] ?: return Result.failure(Exception("Missing `key` in parameters"))

    val participantQuery = participantQueryOf(parameters)

    if (participantQuery.isFailure) {
        return Result.failure(participantQuery.exceptionOrNull()!!)
    }

    return Result.success(ParticipantMetricQuery(key, participantQuery.getOrNull()!!))
}