package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.MatchType
import io.github.haydenheroux.scouting.models.event.Events
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

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

object Matches : IntIdTable() {
    val event = reference("event_id", Events)
    val number = integer("number")
    val type = enumerationByName<MatchType>("type", 255)
}

suspend fun ResultRow.toMatch(): Match {
    val matchId: Int = this[Matches.id].value

    val number = this[Matches.number]
    val type = this[Matches.type]
    val metrics = db.findMetrics(matchId)

    return Match(number, type, metrics)
}
