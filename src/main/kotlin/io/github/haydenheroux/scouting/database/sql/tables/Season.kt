package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.tree.Branch
import io.github.haydenheroux.scouting.database.sql.tree.Node
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

data class SeasonNode(val id: Int, val teamId: Int, val year: Int) : Node<Branch<Season>, Season> {

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
        val team = SQLDatabase.getTeamById(teamId).getOrNull()!!
        val robots = SQLDatabase.getRobotsBySeason(this).getOrNull()!!
        val events = SQLDatabase.getEventsBySeason(this).getOrNull()!!

        return SeasonBranch(this, team, robots, events)
    }

    override fun root(): Branch<Season> {
        return SeasonBranch(this, null, emptyList(), emptyList())
    }
}

data class SeasonBranch(
    val season: SeasonNode,
    val team: TeamNode?,
    val robots: List<RobotNode>,
    val events: List<EventNode>
) : Branch<Season> {
    override fun leaf(): Season {
        return Season(season.year, emptyList(), emptyList())
    }

    override suspend fun leaves(): Season {
        val robots = robots.map { robot -> robot.branch().leaf() }
        val events = events.map { event -> event.branch().leaf() }

        return Season(season.year, robots, events)
    }

    override suspend fun subbranch(): Season {
        val robots = robots.map { robot -> robot.branch().subbranch() }
        val events = events.map { event -> event.branch().subbranch() }

        return Season(season.year, robots, events)
    }

    override suspend fun subbranch(depth: Int): Season {
        if (depth == 0) return leaf()
        if (depth == 1) return leaves()

        val robots = robots.map { robot -> robot.branch().subbranch(depth - 1) }
        val events = events.map { event -> event.branch().subbranch(depth - 1) }

        return Season(season.year, robots, events)
    }
}

