package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.tree.Branch
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.models.Season
import io.github.haydenheroux.scouting.models.Team
import io.github.haydenheroux.scouting.models.enums.Region
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object TeamTable : IntIdTable() {
    val number = integer("number")
    val name = varchar("name", 255)
    val region = enumerationByName<Region>("region", 255)
}

data class TeamNode(val id: Int, val number: Int, val name: String, val region: Region) : Node<Branch<Team>, Team> {

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

    override suspend fun tree(): Tree<Branch<Team>, Team> {
        val seasons = SQLDatabase.getSeasonsByTeam(this).getOrNull()!!

        return TeamTree(this, seasons)
    }

    override fun root(): Branch<Team> {
        return TeamBranch(this, emptyList())
    }
}

data class TeamTree(val team: TeamNode, val seasons: List<Node<Branch<Season>, Season>>) : Tree<Branch<Team>, Team> {
    override suspend fun branch(): Branch<Team> {
        val seasons = seasons.map { it.tree() }

        return TeamBranch(team, seasons)
    }
}

data class TeamBranch(val team: TeamNode, val seasons: List<Tree<Branch<Season>, Season>>) : Branch<Team> {
    override fun leaf(): Team {
        return Team(team.number, team.name, team.region, emptyList())
    }

    override suspend fun leaves(): Team {
        val seasons = seasons.map { season -> season.branch().leaf() }

        return Team(team.number, team.name, team.region, seasons)
    }

    override suspend fun subbranch(): Team {
        val seasons = seasons.map { season -> season.branch().subbranch() }

        return Team(team.number, team.name, team.region, seasons)
    }

    override suspend fun subbranch(depth: Int): Team {
        if (depth == 0) return leaf()
        if (depth == 1) return leaves()

        val seasons = seasons.map { season -> season.branch().subbranch(depth - 1) }

        return Team(team.number, team.name, team.region, seasons)
    }
}

