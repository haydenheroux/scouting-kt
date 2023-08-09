package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.models.event.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select

object SeasonTable : IntIdTable() {
    val teamId = reference("teamId", TeamTable)
    val year = integer("year")
}

object SeasonEventTable : Table() {
    val seasonId = reference("seasonId", SeasonTable)
    val eventId = reference("eventId", EventTable)

    override val primaryKey = PrimaryKey(seasonId, eventId)
}

@Serializable
data class SeasonProperties(val year: Int)

fun ResultRow.seasonProperties(): SeasonProperties {
    val year = this[SeasonTable.year]

    return SeasonProperties(year)
}

data class SeasonReference(
    val year: Int,
    val teamReference: TeamReference?,
    val eventReferences: List<EventReference>,
    val robotReferences: List<RobotReference>
)

suspend fun ResultRow.seasonReference(noParent: Boolean, noChildren: Boolean): SeasonReference {
    val properties = this.seasonProperties()

    val teamId = this[SeasonTable.teamId]
    val teamReference = if (noParent) null else query {
        TeamTable.select { TeamTable.id eq teamId }.map { it.teamReference(true) }.single()
    }

    val seasonId = this[SeasonTable.id]
    val eventReferences = if (noChildren) listOf() else query {
        SeasonEventTable.select { SeasonEventTable.seasonId eq seasonId }.map { row ->
            val eventId = row[SeasonEventTable.eventId]

            EventTable.select { EventTable.id eq eventId }.map { it.eventReference(false) }.single()
        }
    }
    val robotReferences = if (noChildren) listOf() else query {
        RobotTable.select { RobotTable.seasonId eq seasonId }.map { it.robotReference(false) }
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

data class SeasonQuery(val year: Int, val team: TeamQuery)

fun Season.query(teamQuery: TeamQuery): SeasonQuery {
    return SeasonQuery(year, teamQuery)
}

fun Parameters.seasonQuery(): Result<SeasonQuery> {
    val year = this["year"] ?: return Result.failure(Exception("Missing `year` in parameters"))

    val team = this.teamQuery()

    if (team.isFailure) {
        return Result.failure(team.exceptionOrNull()!!)
    }

    return Result.success(SeasonQuery(year.toInt(), team.getOrNull()!!))
}
