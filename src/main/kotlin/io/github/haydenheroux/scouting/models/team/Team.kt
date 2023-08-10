package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.Region
import io.github.haydenheroux.scouting.models.interfaces.Node
import io.github.haydenheroux.scouting.models.interfaces.Parent
import io.github.haydenheroux.scouting.models.interfaces.Subtree
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object TeamTable : IntIdTable() {
    val number = integer("number")
    val name = varchar("name", 255)
    val region = enumerationByName<Region>("region", 255)
}

data class TeamNode(val id: Int, val number: Int, val name: String, val region: Region) : Node<TeamTree> {

    companion object {
        fun from(teamRow: ResultRow): TeamNode {
            return TeamNode(
                teamRow[TeamTable.id].value,
                teamRow[TeamTable.number],
                teamRow[TeamTable.name],
                teamRow[TeamTable.region]
            )
        }
    }

    override suspend fun parent(): Parent<TeamTree>? {
        return null
    }

    override suspend fun subtree(): Subtree<TeamTree> {
        val seasons = db.getSeasonsByTeam(this)

        return TeamSubtree(this, seasons)
    }

    override fun tree(): TeamTree {
        return TeamTree(this, emptyList())
    }
}

data class TeamSubtree(val team: TeamNode, val seasons: List<Node<SeasonTree>>) : Subtree<TeamTree> {
    override suspend fun parent(): Parent<TeamTree>? {
        return team.parent()
    }

    override suspend fun tree(): TeamTree {
        val seasons = seasons.map { it.subtree() }

        return TeamTree(team, seasons)
    }
}

data class TeamTree(val team: TeamNode, val seasons: List<Subtree<SeasonTree>>) {
    fun noChildren(): Team {
        return Team(team.number, team.name, team.region, emptyList())
    }

    suspend fun children(): Team {
        val seasons = seasons.map { season -> season.tree().noChildren() }

        return Team(team.number, team.name, team.region, seasons)
    }

    suspend fun subChildren(): Team {
        val seasons = seasons.map { season -> season.tree().subChildren() }

        return Team(team.number, team.name, team.region, seasons)
    }
}

@Serializable
data class Team(val number: Int, val name: String, val region: Region, val seasons: List<Season>)

data class TeamQuery(val number: Int)

fun teamQueryOf(team: Team): TeamQuery {
    return TeamQuery(team.number)
}

fun Parameters.teamQuery(): Result<TeamQuery> {
    val number = this["team"] ?: return Result.failure(Exception("Missing `team` in parameters"))

    return Result.success(TeamQuery(number.toInt()))
}
