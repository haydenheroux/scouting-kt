package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.interfaces.Node
import io.github.haydenheroux.scouting.models.interfaces.Parent
import io.github.haydenheroux.scouting.models.interfaces.Subtree
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object RobotTable : IntIdTable() {
    val seasonId = reference("seasonId", SeasonTable)
    val name = varchar("name", 255)
}

data class RobotNode(val id: Int, val name: String) : Node<RobotTree> {

    companion object {
        fun from(robotRow: ResultRow): RobotNode {
            return RobotNode(
                robotRow[RobotTable.id].value,
                robotRow[RobotTable.name]
            )
        }
    }

    override suspend fun parent(): Parent<RobotTree> {
        val season = db.getSeasonByRobot(this)

        return RobotParent(this, season)
    }

    override suspend fun subtree(): Subtree<RobotTree> {
        return RobotSubtree(this)
    }

    override fun tree(): RobotTree {
        return RobotTree(this)
    }
}

data class RobotParent(val robot: RobotNode, val season: SeasonNode) : Parent<RobotTree> {
    override suspend fun subtree(): Subtree<RobotTree> {
        return robot.subtree()
    }

    override fun tree(): RobotTree {
        return robot.tree()
    }
}

data class RobotSubtree(val robot: RobotNode) : Subtree<RobotTree> {
    override suspend fun parent(): Parent<RobotTree> {
        return robot.parent()
    }

    override suspend fun tree(): RobotTree {
        return RobotTree(robot)
    }
}

data class RobotTree(val robot: RobotNode) {
    fun noChildren(): Robot {
        return Robot(robot.name)
    }

    fun children(): Robot {
        return Robot(robot.name)
    }

    fun subChildren(): Robot {
        return Robot(robot.name)
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
