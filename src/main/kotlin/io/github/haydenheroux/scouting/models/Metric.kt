package io.github.haydenheroux.scouting.models

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Metric(val key: String, val value: String)

data class MetricQuery(val key: String, val participantQuery: ParticipantQuery)

fun metricQueryOf(metric: Metric, participant: Participant, match: Match, event: Event): MetricQuery {
    val teamQuery = TeamQuery(participant.teamNumber)
    val matchQuery = matchQueryOf(match, event)
    val participantQuery = ParticipantQuery(teamQuery, matchQuery)

    return MetricQuery(metric.key, participantQuery)
}

fun metricQueryOf(parameters: Parameters): Result<MetricQuery> {
    val key = parameters["key"] ?: return Result.failure(Exception("Missing `key` in parameters"))

    val participantQuery = participantQueryOf(parameters)

    if (participantQuery.isFailure) {
        return Result.failure(participantQuery.exceptionOrNull()!!)
    }

    return Result.success(MetricQuery(key, participantQuery.getOrNull()!!))
}