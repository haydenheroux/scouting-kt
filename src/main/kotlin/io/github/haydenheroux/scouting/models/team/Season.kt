package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.event.Event
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

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
data class Season(val year: Int, val robots: List<Robot>, val events: List<Event>)

object Seasons : IntIdTable() {
    val team = reference("team_id", Teams)
    val year = integer("year")
}

suspend fun ResultRow.toSeason(): Season {
    val seasonId: Int = this[Seasons.id].value

    val year = this[Seasons.year]
    val robots = db.findRobots(seasonId)
    val events = db.findEvents(seasonId)

    return Season(year, robots, events)
}
