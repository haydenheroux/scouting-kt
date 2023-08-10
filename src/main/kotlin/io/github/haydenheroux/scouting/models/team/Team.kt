package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.Region
import io.github.haydenheroux.scouting.models.interfaces.Branch
import io.github.haydenheroux.scouting.models.interfaces.Node
import io.github.haydenheroux.scouting.models.interfaces.Parent
import io.github.haydenheroux.scouting.models.interfaces.Tree
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object TeamTable : IntIdTable() {
    val number = integer("number")
    val name = varchar("name", 255)
    val region = enumerationByName<Region>("region", 255)
}

data class TeamNode(val id: Int, val number: Int, val name: String, val region: Region) : Node<Tree<Team>, Team> {

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

    override suspend fun parent(): Parent<Tree<Team>, Team>? {
        return null
    }

    override suspend fun branch(): Branch<Tree<Team>, Team> {
        val seasons = db.getSeasonsByTeam(this)

        return TeamBranch(this, seasons)
    }

    override fun tree(): Tree<Team> {
        return TeamTree(this, emptyList())
    }
}

data class TeamBranch(val team: TeamNode, val seasons: List<Node<Tree<Season>, Season>>) : Branch<Tree<Team>, Team> {
    override suspend fun parent(): Parent<Tree<Team>, Team>? {
        return team.parent()
    }

    override suspend fun tree(): Tree<Team> {
        val seasons = seasons.map { it.branch() }

        return TeamTree(team, seasons)
    }
}

data class TeamTree(val team: TeamNode, val seasons: List<Branch<Tree<Season>, Season>>) : Tree<Team> {
    override fun leaf(): Team {
        return Team(team.number, team.name, team.region, emptyList())
    }

    override suspend fun leaves(): Team {
        val seasons = seasons.map { season -> season.tree().leaf() }

        return Team(team.number, team.name, team.region, seasons)
    }

    override suspend fun subtree(): Team {
        val seasons = seasons.map { season -> season.tree().subtree() }

        return Team(team.number, team.name, team.region, seasons)
    }

    override suspend fun subtree(depth: Int): Team {
        if (depth == 0) return leaf()
        if (depth == 1) return leaves()

        val seasons = seasons.map { season -> season.tree().subtree(depth - 1) }

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
