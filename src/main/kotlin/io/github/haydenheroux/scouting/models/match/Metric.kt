package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.interfaces.Node
import io.github.haydenheroux.scouting.models.interfaces.Parent
import io.github.haydenheroux.scouting.models.interfaces.Subtree
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object MetricTable : IntIdTable() {
    val participantId = reference("participantId", ParticipantTable)
    val key = varchar("key", 255)
    val value = varchar("value", 255)
}

data class MetricNode(val id: Int, val key: String, val value: String) : Node<MetricTree> {

    companion object {
        fun from(metricRow: ResultRow): MetricNode {
            return MetricNode(
                metricRow[MetricTable.id].value,
                metricRow[MetricTable.key],
                metricRow[MetricTable.value]
            )
        }
    }

    override suspend fun parent(): Parent<MetricTree> {
        val participant = db.getParticipantByMetric(this)

        return MetricParent(this, participant)
    }

    override suspend fun subtree(): Subtree<MetricTree> {
        return MetricSubtree(this)
    }

    override fun tree(): MetricTree {
        return MetricTree(this)
    }
}

data class MetricParent(val metric: MetricNode, val participant: ParticipantNode) : Parent<MetricTree> {
    override suspend fun subtree(): Subtree<MetricTree> {
        return metric.subtree()
    }

    override fun tree(): MetricTree {
        return metric.tree()
    }
}

data class MetricSubtree(val metric: MetricNode) : Subtree<MetricTree> {
    override suspend fun parent(): Parent<MetricTree> {
        return metric.parent()
    }

    override suspend fun tree(): MetricTree {
        return MetricTree(metric)
    }
}

data class MetricTree(val metric: MetricNode) {
    fun noChildren(): MetricDTO {
        return MetricDTO(metric.key, metric.value)
    }

    fun children(): MetricDTO {
        return MetricDTO(metric.key, metric.value)
    }

    fun subChildren(): MetricDTO {
        return MetricDTO(metric.key, metric.value)
    }
}

@Serializable
data class MetricDTO(val key: String, val value: String)
