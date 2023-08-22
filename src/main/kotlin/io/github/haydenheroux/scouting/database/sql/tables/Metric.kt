package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.excludes.Exclude
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.models.Metric
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object MetricTable : IntIdTable() {
    val key = varchar("key", 255)
    val value = varchar("value", 2048)
}

data class MetricNode(val id: Int, val key: String, val value: String) :
    Node<Tree<Metric>, Metric> {

    companion object {
        fun from(metricRow: ResultRow): MetricNode {
            return MetricNode(
                metricRow[MetricTable.id].value,
                metricRow[MetricTable.key],
                metricRow[MetricTable.value]
            )
        }
    }

    override suspend fun tree(parent: Boolean): Tree<Metric> {
        return MetricTree(this)
    }

    override fun leaf(): Metric {
        return createMetric(this)
    }
}

data class MetricTree(val metric: MetricNode) : Tree<Metric> {

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