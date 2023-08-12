package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.tree.Branch
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
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

    override suspend fun tree(): Tree<Branch<Robot>, Robot> {
        val season = SQLDatabase.getSeasonById(seasonId).getOrNull()!!

        return RobotTree(this, season)
    }

    override fun root(): Branch<Robot> {
        return RobotBranch(this)
    }
}

data class RobotTree(val robot: RobotNode, val season: SeasonNode) : Tree<Branch<Robot>, Robot> {
    override suspend fun branch(): Branch<Robot> {
        return RobotBranch(robot)
    }
}

data class RobotBranch(val robot: RobotNode) : Branch<Robot> {
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

