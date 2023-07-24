package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.models.enums.MatchType
import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.event.Events
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.dao.id.IntIdTable

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
    @Transient var parent: Event? = null,
    val number: Int,
    val type: MatchType,
    val metrics: List<Metric>
)

object Matches : IntIdTable() {
    val event = reference("event_id", Events)
    val number = integer("number")
    val type = enumerationByName<MatchType>("type", 255)
}
