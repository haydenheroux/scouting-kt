package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.query.SeasonQuery
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * A robot is a robot that is built for FRC.
 *
 * Each robot has properties, many of which are specific to the game that
 * takes place during each season.
 *
 * @property name the name of the robot.
 */
@Serializable
data class Robot(@Transient var season: SeasonQuery? = null, val name: String)

object Robots : IntIdTable() {
    val season = reference("season_id", Seasons)
    val name = varchar("name", 255)
}
