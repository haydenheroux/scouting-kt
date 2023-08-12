package io.github.haydenheroux.scouting.models

import io.github.haydenheroux.scouting.database.sql.tables.MetricNode
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import kotlinx.serialization.Serializable

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