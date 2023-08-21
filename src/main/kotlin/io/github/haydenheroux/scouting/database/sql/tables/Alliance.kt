package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.excludes.Exclude
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.models.Alliance
import io.github.haydenheroux.scouting.models.enums.AllianceColor
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object AllianceTable : IntIdTable() {
    val matchId = reference("matchId", MatchTable)
    val color = enumerationByName<AllianceColor>("color", 255)
}

object AllianceMetricTable : Table() {
    val allianceId = reference("allianceId", AllianceTable)
    val metricId = reference("metricId", MetricTable)

    override val primaryKey = PrimaryKey(allianceId, metricId)
}

data class AllianceNode(
    val id: Int,
    val matchId: Int,
    val color: AllianceColor
) : Node<Tree<Alliance>, Alliance> {

    companion object {
        fun from(allianceRow: ResultRow): AllianceNode {
            return AllianceNode(
                allianceRow[AllianceTable.id].value,
                allianceRow[AllianceTable.matchId].value,
                allianceRow[AllianceTable.color],
            )
        }
    }

    override suspend fun tree(parent: Boolean): Tree<Alliance> {
        TODO("Not yet implemented")
    }

    override fun leaf(): Alliance {
        TODO("Not yet implemented")
    }

}

data class AllianceTree(
    val alliance: AllianceNode,
    val match: MatchNode?,
    val participants: List<ParticipantNode>
) : Tree<Alliance> {

    override suspend fun leaves(): Alliance {
        TODO("Not yet implemented")
    }

    override suspend fun subtree(): Alliance {
        TODO("Not yet implemented")
    }

    override suspend fun subtree(depth: Int, excludes: List<Exclude>): Alliance {
        TODO("Not yet implemented")
    }

}