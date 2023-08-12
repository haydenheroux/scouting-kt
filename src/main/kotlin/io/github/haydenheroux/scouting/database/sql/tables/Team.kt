package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.models.Team
import io.github.haydenheroux.scouting.models.enums.Region
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

    override suspend fun tree(): Tree<Team> {
        val seasons = SQLDatabase.getSeasonsByTeam(this).getOrNull()!!

        return TeamTree(this, seasons)
    }

    override fun root(): Tree<Team> {
        return TeamTree(this, emptyList())
    }
}

data class TeamTree(val team: TeamNode, val seasons: List<SeasonNode>) : Tree<Team> {
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

