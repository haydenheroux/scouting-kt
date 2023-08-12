package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.tree.Branch
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.models.Event
import io.github.haydenheroux.scouting.models.Robot
import io.github.haydenheroux.scouting.models.Season
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

data class SeasonNode(val id: Int, val teamId: Int, val year: Int) : Node<Tree<Season>, Season> {

    companion object {
        fun from(seasonRow: ResultRow): SeasonNode {
            return SeasonNode(
                seasonRow[SeasonTable.id].value,
                seasonRow[SeasonTable.teamId].value,
                seasonRow[SeasonTable.year]
            )
        }
    }

    override suspend fun branch(): SeasonBranch {
        val team = SQLDatabase.getTeamBySeason(this).getOrNull()!!
        val robots = SQLDatabase.getRobotsBySeason(this).getOrNull()!!
        val events = SQLDatabase.getEventsBySeason(this).getOrNull()!!

        return SeasonBranch(this, team, robots, events)
    }

    override fun tree(): Tree<Season> {
        return SeasonTree(this, emptyList(), emptyList())
    }
}

data class SeasonBranch(
    val season: SeasonNode,
    val team: TeamNode,
    val robots: List<RobotNode>,
    val events: List<EventNode>
) :
    Branch<Tree<Season>, Season> {

    override suspend fun tree(): Tree<Season> {
        val robots = robots.map { it.branch() }
        val events = events.map { it.branch() }

        return SeasonTree(season, robots, events)
    }
}

data class SeasonTree(
    val season: SeasonNode,
    val robots: List<Branch<Tree<Robot>, Robot>>,
    val events: List<Branch<Tree<Event>, Event>>
) : Tree<Season> {
    override fun leaf(): Season {
        return Season(season.year, emptyList(), emptyList())
    }

    override suspend fun leaves(): Season {
        val robots = robots.map { robot -> robot.tree().leaf() }
        val events = events.map { event -> event.tree().leaf() }

        return Season(season.year, robots, events)
    }

    override suspend fun subtree(): Season {
        val robots = robots.map { robot -> robot.tree().subtree() }
        val events = events.map { event -> event.tree().subtree() }

        return Season(season.year, robots, events)
    }

    override suspend fun subtree(depth: Int): Season {
        if (depth == 0) return leaf()
        if (depth == 1) return leaves()

        val robots = robots.map { robot -> robot.tree().subtree(depth - 1) }
        val events = events.map { event -> event.tree().subtree(depth - 1) }

        return Season(season.year, robots, events)
    }
}

