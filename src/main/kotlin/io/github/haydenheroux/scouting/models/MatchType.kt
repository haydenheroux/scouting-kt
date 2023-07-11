package io.github.haydenheroux.scouting.models

/**
 * A type of match.
 *
 * Each match must be either a qualification match, playoff match, or
 * other match. Other matches are typically practice matches, replays, or
 * non-competition matches.
 *
 * @see Match
 */
enum class MatchType {
    QUALIFICATION,
    PLAYOFF,
    OTHER
}
