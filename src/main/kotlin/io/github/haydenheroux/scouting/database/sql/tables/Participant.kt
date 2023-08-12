package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.tree.Branch
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.models.Metric
import io.github.haydenheroux.scouting.models.Participant
import io.github.haydenheroux.scouting.models.enums.Alliance
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object ParticipantTable : IntIdTable() {
    val matchId = reference("matchId", MatchTable)
    val alliance = enumerationByName<Alliance>("alliance", 255)
    val teamNumber = integer("teamNumber")
}

data class ParticipantNode(val id: Int, val matchId: Int, val alliance: Alliance, val teamNumber: Int) :
    Node<Tree<Participant>, Participant> {

    companion object {
        fun from(participantRow: ResultRow): ParticipantNode {
            return ParticipantNode(
                participantRow[ParticipantTable.id].value,
                participantRow[ParticipantTable.matchId].value,
                participantRow[ParticipantTable.alliance],
                participantRow[ParticipantTable.teamNumber]
            )
        }
    }

    override suspend fun branch(): Branch<Tree<Participant>, Participant> {
        val match = SQLDatabase.getMatchByParticipant(this).getOrNull()!!
        val metrics = SQLDatabase.getMetricsByParticipant(this).getOrNull()!!

        return ParticipantBranch(this, match, metrics)
    }

    override fun tree(): Tree<Participant> {
        return ParticipantTree(this, emptyList())
    }
}

data class ParticipantBranch(
    val participant: ParticipantNode,
    val match: MatchNode,
    val metrics: List<MetricNode>
) :
    Branch<Tree<Participant>, Participant> {

    override suspend fun tree(): Tree<Participant> {
        val metrics = metrics.map { it.branch() }

        return ParticipantTree(participant, metrics)
    }
}

data class ParticipantTree(
    val participant: ParticipantNode,
    val metrics: List<Branch<Tree<Metric>, Metric>>
) :
    Tree<Participant> {
    override fun leaf(): Participant {
        return Participant(participant.alliance, participant.teamNumber, emptyList())
    }

    override suspend fun leaves(): Participant {
        val metrics = metrics.map { metric -> metric.tree().leaf() }

        return Participant(participant.alliance, participant.teamNumber, metrics)
    }

    override suspend fun subtree(): Participant {
        val metrics = metrics.map { metric -> metric.tree().subtree() }

        return Participant(participant.alliance, participant.teamNumber, metrics)
    }

    override suspend fun subtree(depth: Int): Participant {
        if (depth == 0) return leaf()
        if (depth == 1) return leaves()

        val metrics = metrics.map { metric -> metric.tree().subtree(depth - 1) }

        return Participant(participant.alliance, participant.teamNumber, metrics)
    }
}

