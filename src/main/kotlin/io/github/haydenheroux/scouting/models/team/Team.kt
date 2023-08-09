package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.Region
import io.github.haydenheroux.scouting.models.interfaces.Data
import io.github.haydenheroux.scouting.models.interfaces.Parented
import io.github.haydenheroux.scouting.models.interfaces.Reference
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object TeamTable : IntIdTable() {
    val number = integer("number")
    val name = varchar("name", 255)
    val region = enumerationByName<Region>("region", 255)
}

data class TeamData(val teamId: Int, val number: Int, val name: String, val region: Region) : Data<Team> {

    companion object {
        fun from(teamRow: ResultRow): TeamData {
            return TeamData(
                teamRow[TeamTable.id].value,
                teamRow[TeamTable.number],
                teamRow[TeamTable.name],
                teamRow[TeamTable.region]
            )
        }
    }

    override suspend fun parent(): Parented<Team>? {
        return null
    }

    override suspend fun reference(): Reference<Team> {
        val seasonData = db.getSeasonsByTeam(this)

        return TeamReference(this, seasonData)
    }

    override fun data(): Team {
        return Team(this, emptyList())
    }
}

data class TeamReference(val teamData: TeamData, val seasonData: List<Data<Season>>) : Reference<Team> {
    override suspend fun parent(): Parented<Team>? {
        return teamData.parent()
    }

    override suspend fun dereference(): Team {
        val seasonReferences = seasonData.map { it.reference() }

        return Team(teamData, seasonReferences)
    }
}

data class Team(val teamData: TeamData, val seasonReferences: List<Reference<Season>>)

@Serializable
data class TeamDTO(val number: Int, val name: String, val region: Region, val seasons: List<SeasonDTO>)

data class TeamQuery(val number: Int)

fun teamQueryOf(teamDTO: TeamDTO): TeamQuery {
    return TeamQuery(teamDTO.number)
}

fun Parameters.teamQuery(): Result<TeamQuery> {
    val number = this["team"] ?: return Result.failure(Exception("Missing `team` in parameters"))

    return Result.success(TeamQuery(number.toInt()))
}
