package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.models.event.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

object Seasons : IntIdTable() {
    val team = reference("team_id", Teams)
    val year = integer("year")
}

@Serializable
data class SeasonProperties(val year: Int)

fun ResultRow.seasonProperties(): SeasonProperties {
    val year = this[Seasons.year]

    return SeasonProperties(year)
}

data class SeasonReference(
    val year: Int,
    val teamReference: TeamReference?,
    val eventReferences: List<EventReference>,
    val robotReferences: List<RobotReference>
)

suspend fun ResultRow.asSeasonReference(noParent: Boolean, noChildren: Boolean): SeasonReference {
    val properties = this.seasonProperties()

    val teamId = this[Seasons.team]
    val teamReference = if (noParent) null else query {
        Teams.select { Teams.id eq teamId }.map { it.asTeamReference(true) }.single()
    }

    val seasonId = this[Seasons.id]
    val eventReferences = if (noChildren) listOf() else query {
        SeasonEvents.select { SeasonEvents.season eq seasonId }.map { row ->
            val eventId = row[SeasonEvents.event]

            Events.select { Events.id eq eventId }.map { it.asEventReference(false) }.single()
        }
    }
    val robotReferences = if (noChildren) listOf() else query {
        Robots.select { Robots.season eq seasonId }.map { it.asRobotReference(false) }
    }

    return SeasonReference(properties.year, teamReference, eventReferences, robotReferences)
}

fun SeasonReference.dereference(): Season {
    val events = eventReferences.map { it.dereference() }
    val robots = robotReferences.map { it.dereference() }
    return Season(year, events, robots)
}

@Serializable
data class Season(val year: Int, val events: List<Event>, val robots: List<Robot>)
