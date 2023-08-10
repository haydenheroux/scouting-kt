package io.github.haydenheroux.scouting.models.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A type of match.
 *
 * Each match must be either a qualification match, playoff match, or
 * other match. Other matches are typically practice matches, replays, or
 * non-competition matches.
 */
@Serializable
enum class MatchType {
    @SerialName("qm")
    QUALIFICATION,

    @SerialName("qf")
    QUARTER_FINAL,

    @SerialName("sf")
    SEMI_FINAL,

    @SerialName("f")
    FINAL,

    @SerialName("other")
    OTHER
}

val matchTypeOf = mapOf(
    "qm" to MatchType.QUALIFICATION,
    "qf" to MatchType.QUARTER_FINAL,
    "sf" to MatchType.SEMI_FINAL,
    "f" to MatchType.FINAL
)