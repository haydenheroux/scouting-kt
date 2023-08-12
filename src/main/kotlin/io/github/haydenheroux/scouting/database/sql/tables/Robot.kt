package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.tree.Branch
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.models.Robot
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object RobotTable : IntIdTable() {
    val seasonId = reference("seasonId", SeasonTable)
    val name = varchar("name", 255)
}

data class RobotNode(val id: Int, val seasonId: Int, val name: String) : Node<Branch<Robot>, Robot> {

    companion object {
        fun from(robotRow: ResultRow): RobotNode {
            return RobotNode(
                robotRow[RobotTable.id].value,
                robotRow[RobotTable.seasonId].value,
                robotRow[RobotTable.name]
            )
        }
    }

    override suspend fun branch(): Branch<Robot> {
        val season = SQLDatabase.getSeasonById(seasonId).getOrNull()!!

        return RobotBranch(this, season)
    }

    override fun root(): Branch<Robot> {
        return RobotBranch(this, null)
    }
}

data class RobotBranch(val robot: RobotNode, val season: SeasonNode?) : Branch<Robot> {
    override fun leaf(): Robot {
        return Robot(robot.name)
    }

    override suspend fun leaves(): Robot {
        return leaf()
    }

    override suspend fun subbranch(): Robot {
        return leaf()
    }

    override suspend fun subbranch(depth: Int): Robot {
        return subbranch()
    }
}

