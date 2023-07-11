package io.github.haydenheroux.scouting.models

/**
 * A match played at an FRC event.
 *
 * Each match takes place at an FRC competition. Each match is numbered
 * according to its order in the FRC schedule, beginning at one. Each match
 * may be a qualification match, a playoff match, or other type of match.
 * Each match has metrics that quantitatively describe the match.
 *
 * @property event the event where the match was played.
 * @property number the number of the match.
 * @property type the type of the match.
 * @property metrics the metrics that describe the match.
 * @see MatchType
 */
data class Match(
    val event: Event,
    val number: Int,
    val type: MatchType,
    val metrics: MutableMap<Robot, GameSpecificMetrics>
)
