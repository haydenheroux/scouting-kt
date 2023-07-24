package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.models.event.Event
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * A season is one year of competition for an FRC team.
 *
 * Each season takes place during a single year. During each season, an
 * FRC team must build a number of robots, and participate in a number of
 * events.
 *
 * @property year the year that the season takes place in.
 * @property robots the robots that the team built during the season.
 * @property events the events that the team participated in during the
 *     season.
 * @see Robot
 * @see Event
 */
@Serializable
data class Season(@Transient var team: Team? = null, val year: Int, val robots: List<Robot>, val events: List<Event>)

object Seasons : IntIdTable() {
    val team = reference("team_id", Teams)
    val year = integer("year")
}
