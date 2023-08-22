package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.excludes.Exclude
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.errors.Error
import io.github.haydenheroux.scouting.errors.Success
import io.github.haydenheroux.scouting.models.Metric
import io.github.haydenheroux.scouting.models.Participant
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object ParticipantTable : IntIdTable() {
    val allianceId = reference("allianceId", AllianceTable)
    val teamNumber = integer("teamNumber")
}

object ParticipantMetricTable : Table() {
    val participantId = reference("participantId", ParticipantTable)
    val metricId = reference("metricId", MetricTable)

    override val primaryKey = PrimaryKey(participantId, metricId)
}

data class ParticipantNode(val id: Int, val allianceId: Int, val teamNumber: Int) :
    Node<Tree<Participant>, Participant> {

    companion object {
        fun from(participantRow: ResultRow): ParticipantNode {
            return ParticipantNode(
                participantRow[ParticipantTable.id].value,
                participantRow[ParticipantTable.allianceId].value,
                participantRow[ParticipantTable.teamNumber]
            )
        }
    }

    override suspend fun tree(parent: Boolean): Tree<Participant> {
        val allianceOrError = if (parent) SQLDatabase.getAllianceById(allianceId) else Success(null)
        val metricsOrError = SQLDatabase.getMetricsByParticipant(this)

        val alliance = when (allianceOrError) {
            is Success -> allianceOrError.value
            is Error -> null
        }

        val metrics = when (metricsOrError) {
            is Success -> metricsOrError.value
            is Error -> null
        }

        return ParticipantTree(this, alliance, metrics!!)
    }

    override fun leaf(): Participant {
        return createParticipant(this, emptyList())
    }
}

data class ParticipantTree(
    val participant: ParticipantNode,
    val alliance: AllianceNode?,
    val metrics: List<MetricNode>
) :
    Tree<Participant> {
    override suspend fun subtree(): Participant {
        val metrics = metrics.map { metric -> metric.tree(false).subtree() }

        return createParticipant(participant, metrics)
    }

    override suspend fun subtree(depth: Int, excludes: List<Exclude>): Participant {
        if (depth == 0) return participant.leaf()

        val metrics = if (Exclude.PARTICIPANT_METRICS in excludes) emptyList() else metrics.map { metric ->
            metric.tree(false).subtree(depth - 1, excludes)
        }

        return createParticipant(participant, metrics)
    }
}

fun createParticipant(participant: ParticipantNode, metrics: List<Metric>): Participant {
    return Participant(participant.teamNumber, metrics)
}