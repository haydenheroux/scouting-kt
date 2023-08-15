package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.excludes.Exclude
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.errors.Error
import io.github.haydenheroux.scouting.errors.Success
import io.github.haydenheroux.scouting.models.Robot
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object RobotTable : IntIdTable() {
    val seasonId = reference("seasonId", SeasonTable)
    val name = varchar("name", 255)
}

data class RobotNode(val id: Int, val seasonId: Int, val name: String) : Node<Tree<Robot>, Robot> {

    companion object {
        fun from(robotRow: ResultRow): RobotNode {
            return RobotNode(
                robotRow[RobotTable.id].value,
                robotRow[RobotTable.seasonId].value,
                robotRow[RobotTable.name]
            )
        }
    }

    override suspend fun tree(parent: Boolean): Tree<Robot> {
        val seasonOrError = if (parent) SQLDatabase.getSeasonById(seasonId) else Success(null)

        val season = when (seasonOrError) {
            is Success -> seasonOrError.value
            is Error -> null
        }

        return RobotTree(this, season)
    }

    override fun leaf(): Robot {
        return createRobot(this)
    }
}

data class RobotTree(val robot: RobotNode, val season: SeasonNode?) : Tree<Robot> {
    override suspend fun leaves(): Robot {
        return robot.leaf()
    }

    override suspend fun subtree(): Robot {
        return robot.leaf()
    }

    override suspend fun subtree(depth: Int, excludes: List<Exclude>): Robot {
        return robot.leaf()
    }
}

fun createRobot(robot: RobotNode): Robot {
    return Robot(robot.name)
}