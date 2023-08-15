package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.excludes.Exclude
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.errors.Error
import io.github.haydenheroux.scouting.errors.Success
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
        val participantOrError = if (parent) SQLDatabase.getParticipantById(participantId) else Success(null)

        val participant = when (participantOrError) {
            is Success -> participantOrError.value
            is Error -> null
        }

        return MetricTree(this, participant!!)
    }

    override fun leaf(): Metric {
        return createMetric(this)
    }
}

data class MetricTree(val metric: MetricNode, val participant: ParticipantNode?) : Tree<Metric> {
    override suspend fun leaves(): Metric {
        return metric.leaf()
    }

    override suspend fun subtree(): Metric {
        return metric.leaf()
    }

    override suspend fun subtree(depth: Int, excludes: List<Exclude>): Metric {
        return metric.leaf()
    }
}

fun createMetric(metric: MetricNode): Metric {
    return Metric(metric.key, metric.value)
}