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
    @SerialName("qualification")
    QUALIFICATION,
    @SerialName("playoff")
    PLAYOFF,
    @SerialName("other")
    OTHER
}
