package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.excludes.Exclude
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.errors.Error
import io.github.haydenheroux.scouting.errors.Success
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

    override suspend fun tree(parent: Boolean, excludes: List<Exclude>): Tree<Team> {
        val seasonsOrError =
            if (Exclude.TEAM_SEASONS in excludes) Success(emptyList()) else SQLDatabase.getSeasonsByTeam(this)

        val seasons = when (seasonsOrError) {
            is Success -> seasonsOrError.value
            is Error -> null
        }

        return TeamTree(this, seasons!!)
    }

    override fun leaf(): Team {
        return createTeam(this, emptyList())
    }
}

data class TeamTree(val team: TeamNode, val seasons: List<SeasonNode>) : Tree<Team> {

    override suspend fun subtree(): Team {
        val seasons = seasons.map { season -> season.tree(false, emptyList()).subtree() }

        return createTeam(team, seasons)
    }

    override suspend fun subtree(depth: Int, excludes: List<Exclude>): Team {
        if (depth == 0) return team.leaf()

        val seasons = if (Exclude.TEAM_SEASONS in excludes) emptyList() else seasons.map { season ->
            season.tree(false, excludes).subtree(depth - 1, excludes)
        }

        return createTeam(team, seasons)
    }
}

fun createTeam(team: TeamNode, seasons: List<Season>): Team {
    return Team(team.number, team.name, team.region, seasons)
}