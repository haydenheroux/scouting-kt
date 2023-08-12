package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.sql.db
import io.github.haydenheroux.scouting.database.sql.tree.Branch
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Parent
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object RobotTable : IntIdTable() {
    val seasonId = reference("seasonId", SeasonTable)
    val name = varchar("name", 255)
}

data class RobotNode(val id: Int, val name: String) : Node<Tree<Robot>, Robot> {

    companion object {
        fun from(robotRow: ResultRow): RobotNode {
            return RobotNode(
                robotRow[RobotTable.id].value,
                robotRow[RobotTable.name]
            )
        }
    }

    override suspend fun parent(): Parent<Tree<Robot>, Robot> {
        val season = db.getSeasonByRobot(this).getOrNull()!!

        return RobotParent(this, season)
    }

    override suspend fun branch(): Branch<Tree<Robot>, Robot> {
        return RobotBranch(this)
    }

    override fun tree(): Tree<Robot> {
        return RobotTree(this)
    }
}

data class RobotParent(val robot: RobotNode, val season: SeasonNode) : Parent<Tree<Robot>, Robot> {
    override suspend fun branch(): Branch<Tree<Robot>, Robot> {
        return robot.branch()
    }

    override fun tree(): Tree<Robot> {
        return robot.tree()
    }
}

data class RobotBranch(val robot: RobotNode) : Branch<Tree<Robot>, Robot> {
    override suspend fun parent(): Parent<Tree<Robot>, Robot> {
        return robot.parent()
    }

    override suspend fun tree(): Tree<Robot> {
        return RobotTree(robot)
    }
}

data class RobotTree(val robot: RobotNode) : Tree<Robot> {
    override fun leaf(): Robot {
        return Robot(robot.name)
    }

    override suspend fun leaves(): Robot {
        return leaf()
    }

    override suspend fun subtree(): Robot {
        return leaf()
    }

    override suspend fun subtree(depth: Int): Robot {
        return subtree()
    }
}

@Serializable
data class Robot(val name: String)

data class RobotQuery(val name: String, val season: SeasonQuery)

fun robotQueryOf(robot: Robot, seasonQuery: SeasonQuery): RobotQuery {
    return RobotQuery(robot.name, seasonQuery)
}

fun Parameters.robotQuery(): Result<RobotQuery> {
    val name = this["robot"] ?: return Result.failure(Exception("Missing `robot` in parameters"))

    val season = this.seasonQuery()

    if (season.isFailure) {
        return Result.failure(season.exceptionOrNull()!!)
    }

    return Result.success(RobotQuery(name, season.getOrNull()!!))
}
