package io.github.haydenheroux.scouting.models

import io.github.haydenheroux.scouting.models.enums.MatchType
import io.github.haydenheroux.scouting.models.enums.matchTypeOf
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Match(val set: Int, val number: Int, val type: MatchType, val participants: List<Participant>)
data class MatchQuery(val set: Int, val number: Int, val type: MatchType, val event: EventQuery)

fun matchQueryOf(match: Match, eventQuery: EventQuery): MatchQuery {
    return MatchQuery(match.set, match.number, match.type, eventQuery)
}

fun matchQueryOf(matchKey: String, eventQuery: EventQuery): Result<MatchQuery> {
    val match = parseMatchKey(matchKey).getOrNull() ?: return Result.failure(Exception("Failed parsing match key"))

    return Result.success(MatchQuery(match.set, match.number, match.type, eventQuery))
}

fun Parameters.matchQuery(): Result<MatchQuery> {
    val matchKey = this["match"] ?: return Result.failure(Exception("Missing `match` in parameters"))

    val event = this.eventQuery()

    if (event.isFailure) {
        return Result.failure(event.exceptionOrNull()!!)
    }

    return matchQueryOf(matchKey, event.getOrNull()!!)
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