package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.excludes.Exclude
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object MetricTable : IntIdTable() {
    val key = varchar("key", 255)
    val value = varchar("value", 2048)
}

data class MetricNode(val id: Int, val key: String, val value: String) :
    Node<Tree<Pair<String, String>>, Pair<String, String>> {

    companion object {
        fun from(metricRow: ResultRow): MetricNode {
            return MetricNode(
                metricRow[MetricTable.id].value,
                metricRow[MetricTable.key],
                metricRow[MetricTable.value]
            )
        }
    }

    override suspend fun tree(parent: Boolean, excludes: List<Exclude>): Tree<Pair<String, String>> {
        return MetricTree(this)
    }

    override fun leaf(): Pair<String, String> {
        return createMetric(this)
    }
}

data class MetricTree(val metric: MetricNode) : Tree<Pair<String, String>> {

    override suspend fun subtree(): Pair<String, String> {
        return metric.leaf()
    }

    override suspend fun subtree(depth: Int, excludes: List<Exclude>): Pair<String, String> {
        return metric.leaf()
    }
}

fun createMetric(metric: MetricNode): Pair<String, String> {
    return Pair(metric.key, metric.value)
}