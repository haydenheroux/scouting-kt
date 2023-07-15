package io.github.haydenheroux.scouting.models.match

import kotlinx.serialization.Serializable

/**
 * A match played at an FRC event.
 *
 * Each match is numbered according to its order in the FRC schedule,
 * beginning at one. Each match is a qualification match, a playoff match,
 * or other type of match. Each match has some metrics that are collected
 * to assess the match.
 *
 * @property number the number of the match.
 * @property type the type of the match.
 * @property metrics the metrics collected for the match.
 * @see MatchType
 * @see Metric
 */
@Serializable
data class Match(
    val number: Int,
    val type: MatchType,
    val metrics: List<Metric>
)
