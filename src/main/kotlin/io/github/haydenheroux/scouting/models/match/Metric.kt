package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.interfaces.Data
import io.github.haydenheroux.scouting.models.interfaces.Parented
import io.github.haydenheroux.scouting.models.interfaces.Reference
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object MetricTable : IntIdTable() {
    val participantId = reference("participantId", ParticipantTable)
    val key = varchar("key", 255)
    val value = varchar("value", 255)
}

data class MetricData(val metricId: Int, val key: String, val value: String) : Data<Metric> {

    companion object {
        fun from(metricRow: ResultRow): MetricData {
            return MetricData(
                metricRow[MetricTable.id].value,
                metricRow[MetricTable.key],
                metricRow[MetricTable.value]
            )
        }
    }

    override suspend fun parent(): Parented<Metric> {
        val participantData = db.getParticipantByMetric(this)

        return ParentedMetric(this, participantData)
    }

    override suspend fun reference(): Reference<Metric> {
        return MetricReference(this)
    }

    override fun data(): Metric {
        return Metric(this)
    }
}

data class ParentedMetric(val metricData: MetricData, val participantData: ParticipantData) : Parented<Metric> {
    override suspend fun reference(): Reference<Metric> {
        return metricData.reference()
    }

    override fun data(): Metric {
        return metricData.data()
    }
}

data class MetricReference(val metricData: MetricData) : Reference<Metric> {
    override suspend fun parent(): Parented<Metric> {
        return metricData.parent()
    }

    override suspend fun dereference(): Metric {
        return Metric(metricData)
    }
}

data class Metric(val metricData: MetricData)

@Serializable
data class MetricDTO(val key: String, val value: String)
