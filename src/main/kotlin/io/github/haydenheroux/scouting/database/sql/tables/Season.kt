package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.excludes.Exclude
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.errors.Error
import io.github.haydenheroux.scouting.errors.Success
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

    override suspend fun tree(parent: Boolean, excludes: List<Exclude>): SeasonTree {
        val teamOrError = if (parent) SQLDatabase.getTeamById(teamId) else Success(null)
        val robotsOrError =
            if (Exclude.SEASON_ROBOTS in excludes) Success(emptyList()) else SQLDatabase.getRobotsBySeason(this)
        val eventsOrError =
            if (Exclude.SEASON_EVENTS in excludes) Success(emptyList()) else SQLDatabase.getEventsBySeason(this)

        val team = when (teamOrError) {
            is Success -> teamOrError.value
            is Error -> null
        }

        val robots = when (robotsOrError) {
            is Success -> robotsOrError.value
            is Error -> null
        }

        val events = when (eventsOrError) {
            is Success -> eventsOrError.value
            is Error -> null
        }

        return SeasonTree(this, team, robots!!, events!!)
    }

    override fun leaf(): Season {
        return createSeason(this, emptyList(), emptyList())
    }
}

data class SeasonTree(
    val season: SeasonNode,
    val team: TeamNode?,
    val robots: List<RobotNode>,
    val events: List<EventNode>
) : Tree<Season> {
    override suspend fun subtree(): Season {
        val robots = robots.map { robot -> robot.tree(false, emptyList()).subtree() }
        val events = events.map { event -> event.tree(false, emptyList()).subtree() }

        return createSeason(season, robots, events)
    }

    override suspend fun subtree(depth: Int, excludes: List<Exclude>): Season {
        if (depth == 0) return season.leaf()

        val robots = if (Exclude.SEASON_ROBOTS in excludes) emptyList() else robots.map { robot ->
            robot.tree(false, excludes).subtree(depth - 1, excludes)
        }
        val events = if (Exclude.SEASON_EVENTS in excludes) emptyList() else events.map { event ->
            event.tree(false, excludes).subtree(depth - 1, excludes)
        }

        return createSeason(season, robots, events)
    }
}

fun createSeason(season: SeasonNode, robots: List<Robot>, events: List<Event>): Season {
    return Season(season.year, robots, events)
}