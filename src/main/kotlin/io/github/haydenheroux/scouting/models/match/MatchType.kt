package io.github.haydenheroux.scouting.models.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A type of match.
 *
 * Each match must be either a qualification match, playoff match, or
 * other match. Other matches are typically practice matches, replays, or
 * non-competition matches.
 *
 * @see Match
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
