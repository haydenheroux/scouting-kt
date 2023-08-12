package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.models.Metric
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object MetricTable : IntIdTable() {
    val participantId = reference("participantId", ParticipantTable)
    val key = varchar("key", 255)
    val value = varchar("value", 255)
}

data class MetricNode(val id: Int, val participantId: Int, val key: String, val value: String) :
    Node<Tree<Metric>, Metric> {

    companion object {
        fun from(metricRow: ResultRow): MetricNode {
            return MetricNode(
                metricRow[MetricTable.id].value,
                metricRow[MetricTable.participantId].value,
                metricRow[MetricTable.key],
                metricRow[MetricTable.value]
            )
        }
    }

    override suspend fun tree(parent: Boolean): Tree<Metric> {
        val participant = if (parent) SQLDatabase.getParticipantById(participantId).getOrNull()!! else null

        return MetricTree(this, participant)
    }

    override fun leaf(): Metric {
        return Metric(key, value)
    }
}

data class MetricTree(val metric: MetricNode, val participant: ParticipantNode?) : Tree<Metric> {
    override suspend fun leaves(): Metric {
        return metric.leaf()
    }

    override suspend fun subtree(): Metric {
        return metric.leaf()
    }

    override suspend fun subtree(depth: Int): Metric {
        return metric.leaf()
    }
}
