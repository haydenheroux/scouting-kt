package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.excludes.Exclude
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.errors.Error
import io.github.haydenheroux.scouting.errors.Success
import io.github.haydenheroux.scouting.models.Alliance
import io.github.haydenheroux.scouting.models.Participant
import io.github.haydenheroux.scouting.models.enums.AllianceColor
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object AllianceTable : IntIdTable() {
    val matchId = reference("matchId", MatchTable)
    val color = enumerationByName<AllianceColor>("color", 255)
}

object AllianceMetricTable : Table() {
    val allianceId = reference("allianceId", AllianceTable)
    val metricId = reference("metricId", MetricTable)

    override val primaryKey = PrimaryKey(allianceId, metricId)
}

data class AllianceNode(
    val id: Int,
    val matchId: Int,
    val color: AllianceColor
) : Node<Tree<Alliance>, Alliance> {

    companion object {
        fun from(allianceRow: ResultRow): AllianceNode {
            return AllianceNode(
                allianceRow[AllianceTable.id].value,
                allianceRow[AllianceTable.matchId].value,
                allianceRow[AllianceTable.color],
            )
        }
    }

    override suspend fun tree(parent: Boolean, excludes: List<Exclude>): Tree<Alliance> {
        val matchOrError = if (parent) SQLDatabase.getMatchById(matchId) else Success(null)
        val metricsOrError =
            if (Exclude.ALLIANCE_METRICS in excludes) Success(emptyList()) else SQLDatabase.getMetricsByAlliance(this)
        val participantsOrError =
            if (Exclude.ALLIANCE_PARTICIPANTS in excludes) Success(emptyList()) else SQLDatabase.getParticipantsByAlliance(
                this
            )

        val match = when (matchOrError) {
            is Success -> matchOrError.value
            is Error -> null
        }

        val metrics = when (metricsOrError) {
            is Success -> metricsOrError.value
            is Error -> null
        }

        val participants = when (participantsOrError) {
            is Success -> participantsOrError.value
            is Error -> null
        }

        return AllianceTree(this, match, metrics!!, participants!!)
    }

    override fun leaf(): Alliance {
        return createAlliance(this, emptyMap(), emptyList())
    }

}

data class AllianceTree(
    val alliance: AllianceNode,
    val match: MatchNode?,
    val metrics: List<MetricNode>,
    val participants: List<ParticipantNode>
) : Tree<Alliance> {

    override suspend fun subtree(): Alliance {
        val metrics = metrics.map { metric -> metric.tree(false, emptyList()).subtree() }
        val participants = participants.map { participant -> participant.tree(false, emptyList()).subtree() }

        return createAlliance(alliance, metrics.associate { it }, participants)
    }

    override suspend fun subtree(depth: Int, excludes: List<Exclude>): Alliance {
        if (depth == 0) return alliance.leaf()

        val metrics = if (Exclude.ALLIANCE_METRICS in excludes) emptyMap() else metrics.map { metric ->
            metric.tree(false, excludes).subtree(depth - 1, excludes)
        }.associate { it }
        val participants =
            if (Exclude.ALLIANCE_PARTICIPANTS in excludes) emptyList() else participants.map { participant ->
                participant.tree(false, excludes).subtree(depth - 1, excludes)
            }

        return createAlliance(alliance, metrics, participants)
    }

}

fun createAlliance(alliance: AllianceNode, metrics: Map<String, String>, participants: List<Participant>): Alliance {
    return Alliance(alliance.color, metrics, participants)
}