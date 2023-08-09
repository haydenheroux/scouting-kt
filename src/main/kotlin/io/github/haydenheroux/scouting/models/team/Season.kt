package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.event.EventDTO
import io.github.haydenheroux.scouting.models.event.EventData
import io.github.haydenheroux.scouting.models.event.EventTable
import io.github.haydenheroux.scouting.models.interfaces.Data
import io.github.haydenheroux.scouting.models.interfaces.Parented
import io.github.haydenheroux.scouting.models.interfaces.Reference
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object SeasonTable : IntIdTable() {
    val teamId = reference("teamId", TeamTable)
    val year = integer("year")
}

object SeasonEventTable : Table() {
    val seasonId = reference("seasonId", SeasonTable)
    val eventId = reference("eventId", EventTable)

    override val primaryKey = PrimaryKey(seasonId, eventId)
}

data class SeasonData(val seasonId: Int, val year: Int) : Data<Season> {

    companion object {
        fun from(seasonRow: ResultRow): SeasonData {
            return SeasonData(
                seasonRow[SeasonTable.id].value,
                seasonRow[SeasonTable.year]
            )
        }
    }

    override suspend fun parent(): Parented<Season> {
        val teamData = db.getTeamBySeason(this)

        return ParentedSeason(this, teamData)
    }

    override suspend fun reference(): Reference<Season> {
        val robotData = db.getRobotsBySeason(this)
        val eventData = db.getEventsBySeason(this)

        return SeasonReference(this, robotData, eventData)
    }

    override fun data(): Season {
        return Season(this, emptyList(), emptyList())
    }
}

data class ParentedSeason(val seasonData: SeasonData, val teamData: TeamData) : Parented<Season> {
    override suspend fun reference(): Reference<Season> {
        return seasonData.reference()
    }

    override fun data(): Season {
        return seasonData.data()
    }
}

data class SeasonReference(val seasonData: SeasonData, val robotData: List<RobotData>, val eventData: List<EventData>) :
    Reference<Season> {
    override suspend fun parent(): Parented<Season> {
        return seasonData.parent()
    }

    override suspend fun dereference(): Season {
        val robotReferences = robotData.map { it.reference() }
        val eventReferences = eventData.map { it.reference() }

        return Season(seasonData, robotReferences, eventReferences)
    }
}

data class Season(
    val seasonData: SeasonData,
    val robotReferences: List<Reference<Robot>>,
    val eventReferences: List<Reference<Event>>
) {
    fun noChildren(): SeasonDTO {
        return SeasonDTO(seasonData.year, emptyList(), emptyList())
    }

    suspend fun children(): SeasonDTO {
        val robots = robotReferences.map { robotReference -> robotReference.dereference().noChildren() }
        val events = eventReferences.map { eventReference -> eventReference.dereference().noChildren() }

        return SeasonDTO(seasonData.year, robots, events)
    }

    suspend fun subChildren(): SeasonDTO {
        val robots = robotReferences.map { robotReference -> robotReference.dereference().subChildren() }
        val events = eventReferences.map { eventReference -> eventReference.dereference().subChildren() }

        return SeasonDTO(seasonData.year, robots, events)
    }
}

@Serializable
data class SeasonDTO(val year: Int, val robots: List<RobotDTO>, val events: List<EventDTO>)

data class SeasonQuery(val year: Int, val team: TeamQuery)

fun seasonQueryOf(seasonDTO: SeasonDTO, teamQuery: TeamQuery): SeasonQuery {
    return SeasonQuery(seasonDTO.year, teamQuery)
}

fun Parameters.seasonQuery(): Result<SeasonQuery> {
    val year = this["year"] ?: return Result.failure(Exception("Missing `year` in parameters"))

    val team = this.teamQuery()

    if (team.isFailure) {
        return Result.failure(team.exceptionOrNull()!!)
    }

    return Result.success(SeasonQuery(year.toInt(), team.getOrNull()!!))
}
