package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.database.db
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
    val seasonId: Int,
    val teamReference: TeamReference,
    val year: Int,
)

suspend fun ResultRow.seasonReference(): SeasonReference {
    val seasonId = this[SeasonTable.id].value
    val properties = this.seasonProperties()

    val teamId = this[SeasonTable.teamId].value
    val teamReference = db.getTeam(teamId)

    return SeasonReference(seasonId, teamReference, properties.year)
}

suspend fun SeasonReference.dereference(children: Boolean): Season {
    val events = if (children) db.getEvents(this).map { it.dereference(true) } else emptyList()
    val robots = if (children) db.getRobots(this).map { it.dereference(true) } else emptyList()
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
