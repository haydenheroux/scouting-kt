package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.sql.db
import io.github.haydenheroux.scouting.database.sql.tree.Branch
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Parent
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.event.EventNode
import io.github.haydenheroux.scouting.models.event.EventTable
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

data class SeasonNode(val id: Int, val year: Int) : Node<Tree<Season>, Season> {

    companion object {
        fun from(seasonRow: ResultRow): SeasonNode {
            return SeasonNode(
                seasonRow[SeasonTable.id].value,
                seasonRow[SeasonTable.year]
            )
        }
    }

    override suspend fun parent(): SeasonParent {
        val team = db.getTeamBySeason(this).getOrNull()!!

        return SeasonParent(this, team)
    }

    override suspend fun branch(): Branch<Tree<Season>, Season> {
        val robots = db.getRobotsBySeason(this).getOrNull()!!
        val events = db.getEventsBySeason(this).getOrNull()!!

        return SeasonBranch(this, robots, events)
    }

    override fun tree(): Tree<Season> {
        return SeasonTree(this, emptyList(), emptyList())
    }
}

data class SeasonParent(val season: SeasonNode, val team: TeamNode) : Parent<Tree<Season>, Season> {
    override suspend fun branch(): Branch<Tree<Season>, Season> {
        return season.branch()
    }

    override fun tree(): Tree<Season> {
        return season.tree()
    }
}

data class SeasonBranch(val season: SeasonNode, val robots: List<RobotNode>, val events: List<EventNode>) :
    Branch<Tree<Season>, Season> {
    override suspend fun parent(): Parent<Tree<Season>, Season> {
        return season.parent()
    }

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

@Serializable
data class Season(val year: Int, val robots: List<Robot>, val events: List<Event>)

data class SeasonQuery(val year: Int, val team: TeamQuery)

fun seasonQueryOf(season: Season, teamQuery: TeamQuery): SeasonQuery {
    return SeasonQuery(season.year, teamQuery)
}

fun Parameters.seasonQuery(): Result<SeasonQuery> {
    val year = this["year"] ?: return Result.failure(Exception("Missing `year` in parameters"))

    val team = this.teamQuery()

    if (team.isFailure) {
        return Result.failure(team.exceptionOrNull()!!)
    }

    return Result.success(SeasonQuery(year.toInt(), team.getOrNull()!!))
}
