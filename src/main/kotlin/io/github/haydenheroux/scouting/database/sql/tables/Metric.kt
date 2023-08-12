package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.tree.Branch
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

    override suspend fun branch(): Branch<Tree<Metric>, Metric> {
        val participant = SQLDatabase.getParticipantByMetric(this).getOrNull()!!

        return MetricBranch(this, participant)
    }

    override fun tree(): Tree<Metric> {
        return MetricTree(this)
    }
}

data class MetricBranch(val metric: MetricNode, val participant: ParticipantNode) : Branch<Tree<Metric>, Metric> {
    override suspend fun tree(): Tree<Metric> {
        return MetricTree(metric)
    }
}

data class MetricTree(val metric: MetricNode) : Tree<Metric> {
    override fun leaf(): Metric {
        return Metric(metric.key, metric.value)
    }

    override suspend fun leaves(): Metric {
        return leaf()
    }

    override suspend fun subtree(): Metric {
        return leaf()
    }

    override suspend fun subtree(depth: Int): Metric {
        return subtree()
    }
}
