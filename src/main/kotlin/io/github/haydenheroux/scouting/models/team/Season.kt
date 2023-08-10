package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.event.EventNode
import io.github.haydenheroux.scouting.models.event.EventTable
import io.github.haydenheroux.scouting.models.event.EventTree
import io.github.haydenheroux.scouting.models.interfaces.Node
import io.github.haydenheroux.scouting.models.interfaces.Parent
import io.github.haydenheroux.scouting.models.interfaces.Subtree
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

data class SeasonNode(val id: Int, val year: Int) : Node<SeasonTree> {

    companion object {
        fun from(seasonRow: ResultRow): SeasonNode {
            return SeasonNode(
                seasonRow[SeasonTable.id].value,
                seasonRow[SeasonTable.year]
            )
        }
    }

    override suspend fun parent(): Parent<SeasonTree> {
        val team = db.getTeamBySeason(this)

        return SeasonParent(this, team)
    }

    override suspend fun subtree(): Subtree<SeasonTree> {
        val robots = db.getRobotsBySeason(this)
        val events = db.getEventsBySeason(this)

        return SeasonSubtree(this, robots, events)
    }

    override fun tree(): SeasonTree {
        return SeasonTree(this, emptyList(), emptyList())
    }
}

data class SeasonParent(val season: SeasonNode, val team: TeamNode) : Parent<SeasonTree> {
    override suspend fun subtree(): Subtree<SeasonTree> {
        return season.subtree()
    }

    override fun tree(): SeasonTree {
        return season.tree()
    }
}

data class SeasonSubtree(val season: SeasonNode, val robots: List<RobotNode>, val events: List<EventNode>) :
    Subtree<SeasonTree> {
    override suspend fun parent(): Parent<SeasonTree> {
        return season.parent()
    }

    override suspend fun tree(): SeasonTree {
        val robots = robots.map { it.subtree() }
        val events = events.map { it.subtree() }

        return SeasonTree(season, robots, events)
    }
}

data class SeasonTree(
    val season: SeasonNode,
    val robots: List<Subtree<RobotTree>>,
    val events: List<Subtree<EventTree>>
) {
    fun noChildren(): Season {
        return Season(season.year, emptyList(), emptyList())
    }

    suspend fun children(): Season {
        val robots = robots.map { robot -> robot.tree().noChildren() }
        val events = events.map { event -> event.tree().noChildren() }

        return Season(season.year, robots, events)
    }

    suspend fun subChildren(): Season {
        val robots = robots.map { robot -> robot.tree().subChildren() }
        val events = events.map { event -> event.tree().subChildren() }

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
