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
data class SeasonData(val year: Int)

fun ResultRow.asSeasonData(): SeasonData {
    val year = this[Seasons.year]

    return SeasonData(year)
}

data class SeasonReference(
    val seasonData: SeasonData,
    val teamReference: TeamReference?,
    val eventReferences: List<EventReference>?,
    val robotReferences: List<RobotReference>?
)

suspend fun ResultRow.asSeasonReference(orphan: Boolean): SeasonReference {
    val seasonData = this.asSeasonData()

    val teamId = this[Seasons.team]
    val teamReference = if (orphan) null else query {
        Teams.select { Teams.id eq teamId }.map { it.asTeamReference() }.single()
    }

    val seasonId = this[Seasons.id]
    val eventReferences = if (true) null else query {
        SeasonEvents.select { SeasonEvents.season eq seasonId }.map { row ->
            val eventId = row[SeasonEvents.event]

            Events.select { Events.id eq eventId }.map { it.asEventReference() }.single()
        }
    }
    val robotReferences = if (true) null else query {
        Robots.select { Robots.season eq seasonId }.map { it.asRobotReference(false) }
    }

    return SeasonReference(seasonData, teamReference, eventReferences, robotReferences)
}

fun SeasonReference.dereference(): Season {
    // TODO
    // val events = eventReferences!!.map { it.dereference() }
    // val robots = robotReferences!!.map { it.dereference() }
    return Season(seasonData, listOf(), listOf())
}

@Serializable
data class Season(val seasonData: SeasonData, val events: List<Event>, val robots: List<Robot>)
