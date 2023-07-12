package io.github.haydenheroux.scouting.models.match

import kotlinx.serialization.Serializable

/**
 * A match played at an FRC event.
 *
 * Each match is numbered according to its order in the FRC schedule,
 * beginning at one. Each match is a qualification match, a playoff
 * match, or other type of match. Each match is played by some alliances.
 *
 * @property number the number of the match.
 * @property type the type of the match.
 * @property alliances the alliances playing in the match.
 * @see MatchType
 * @see AllianceColor
 * @see Alliance
 */
@Serializable
data class Match(
    val number: Int,
    val type: MatchType,
    val alliances: MutableMap<AllianceColor, Alliance>
)
