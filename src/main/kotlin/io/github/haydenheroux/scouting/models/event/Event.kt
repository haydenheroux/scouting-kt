package io.github.haydenheroux.scouting.models.event

import io.github.haydenheroux.scouting.models.enums.Region
import io.github.haydenheroux.scouting.models.match.Match
import io.github.haydenheroux.scouting.models.team.Seasons
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

/**
 * An event is an FRC competition event.
 *
 * Each event has some properties that identify it. For example, the name
 * and region are identifiable properties. Additionally, each event has
 * the year and week that the event occurred. Weeks are measured according
 * to the FRC schedule, beginning at zero. Finally, each event has matches
 * that are played at the event.
 *
 * @property name the name of the event.
 * @property region the region of the event.
 * @property year the year that the event occurred.
 * @property week the week that the event occurred.
 * @property matches the matches that were played at the event.
 * @see Match
 */
@Serializable
data class Event(
    val name: String,
    val region: Region,
    val year: Int,
    val week: Int,
    val matches: List<Match>
)

object Events : IntIdTable() {
    val name = varchar("name", 255)
    val region = enumerationByName<Region>("region", 255)
    val year = integer("year")
    val week = integer("week")
}

object SeasonEvents : Table() {
    val season = reference("season_id", Seasons)
    val event = reference("event_id", Events)

    override val primaryKey = PrimaryKey(season, event, name = "seasonEvent")
}
