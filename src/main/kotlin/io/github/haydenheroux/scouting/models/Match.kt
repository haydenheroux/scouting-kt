package io.github.haydenheroux.scouting.models

import io.github.haydenheroux.scouting.models.enums.MatchType
import io.github.haydenheroux.scouting.models.enums.matchTypeOf
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Match(
    val set: Int,
    val number: Int,
    val type: MatchType,
    val participants: List<Participant>
)

data class MatchQuery(val set: Int, val number: Int, val type: MatchType, val event: EventQuery)

fun matchQueryOf(match: Match, event: Event): MatchQuery {
    val eventQuery = eventQueryOf(event)

    return MatchQuery(match.set, match.number, match.type, eventQuery)
}

fun matchQueryOf(parameters: Parameters): Result<MatchQuery> {
    val matchKey = parameters["match"] ?: return Result.failure(Exception("Missing `match` in parameters"))

    val eventQuery = eventQueryOf(parameters)

    if (eventQuery.isFailure) {
        return Result.failure(eventQuery.exceptionOrNull()!!)
    }

    val match = parseMatchKey(matchKey).getOrNull() ?: return Result.failure(Exception("Failed parsing match key"))

    return Result.success(MatchQuery(match.set, match.number, match.type, eventQuery.getOrNull()!!))
}

data class MatchKey(val set: Int, val number: Int, val type: MatchType)

fun parseMatchKey(matchKey: String): Result<MatchKey> {
    return runCatching {
        val regex = Regex("(?:.*_)?(qm|qf|sf|f)(\\d{1,2})(?:m(\\d{1,2}))?")

        regex.find(matchKey)?.destructured?.toList()?.let { fields ->
            val isQualificationMatch = fields[0] == "qm"

            if (isQualificationMatch) {
                val set = 1 // default value for set in TBA is 1
                val match = fields[1].toInt()
                val type = matchTypeOf[fields[0]]!!
                MatchKey(set, match, type)
            } else {
                val set = fields[1].toInt()
                val match = fields[2].toInt()
                val type = matchTypeOf[fields[0]]!!
                MatchKey(set, match, type)
            }
        } ?: run {
            throw Exception("Regex failed")
        }
    }
}