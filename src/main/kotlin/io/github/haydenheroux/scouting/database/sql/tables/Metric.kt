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
    Node<Branch<Metric>, Metric> {

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

    override suspend fun tree(): Tree<Branch<Metric>, Metric> {
        val participant = SQLDatabase.getParticipantById(participantId).getOrNull()!!

        return MetricTree(this, participant)
    }

    override fun root(): Branch<Metric> {
        return MetricBranch(this)
    }
}

data class MetricTree(val metric: MetricNode, val participant: ParticipantNode) : Tree<Branch<Metric>, Metric> {
    override suspend fun branch(): Branch<Metric> {
        return MetricBranch(metric)
    }
}

data class MetricBranch(val metric: MetricNode) : Branch<Metric> {
    override fun leaf(): Metric {
        return Metric(metric.key, metric.value)
    }

    override suspend fun leaves(): Metric {
        return leaf()
    }

    override suspend fun subbranch(): Metric {
        return leaf()
    }

    override suspend fun subbranch(depth: Int): Metric {
        return subbranch()
    }
}
