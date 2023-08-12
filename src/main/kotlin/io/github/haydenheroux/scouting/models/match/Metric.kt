package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.sql.db
import io.github.haydenheroux.scouting.database.sql.tree.Branch
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Parent
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object MetricTable : IntIdTable() {
    val participantId = reference("participantId", ParticipantTable)
    val key = varchar("key", 255)
    val value = varchar("value", 255)
}

data class MetricNode(val id: Int, val key: String, val value: String) : Node<Tree<Metric>, Metric> {

    companion object {
        fun from(metricRow: ResultRow): MetricNode {
            return MetricNode(
                metricRow[MetricTable.id].value,
                metricRow[MetricTable.key],
                metricRow[MetricTable.value]
            )
        }
    }

    override suspend fun parent(): Parent<Tree<Metric>, Metric> {
        val participant = db.getParticipantByMetric(this).getOrNull()!!

        return MetricParent(this, participant)
    }

    override suspend fun branch(): Branch<Tree<Metric>, Metric> {
        return MetricBranch(this)
    }

    override fun tree(): Tree<Metric> {
        return MetricTree(this)
    }
}

data class MetricParent(val metric: MetricNode, val participant: ParticipantNode) : Parent<Tree<Metric>, Metric> {
    override suspend fun branch(): Branch<Tree<Metric>, Metric> {
        return metric.branch()
    }

    override fun tree(): Tree<Metric> {
        return metric.tree()
    }
}

data class MetricBranch(val metric: MetricNode) : Branch<Tree<Metric>, Metric> {
    override suspend fun parent(): Parent<Tree<Metric>, Metric> {
        return metric.parent()
    }

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

@Serializable
data class Metric(val key: String, val value: String)
