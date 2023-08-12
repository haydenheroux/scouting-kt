package io.github.haydenheroux.scouting.database.sql.tables

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.database.sql.tree.Branch
import io.github.haydenheroux.scouting.database.sql.tree.Node
import io.github.haydenheroux.scouting.database.sql.tree.Parent
import io.github.haydenheroux.scouting.database.sql.tree.Tree
import io.github.haydenheroux.scouting.models.Metric
import io.github.haydenheroux.scouting.models.Participant
import io.github.haydenheroux.scouting.models.Team
import io.github.haydenheroux.scouting.models.enums.Alliance
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object ParticipantTable : IntIdTable() {
    val matchId = reference("matchId", MatchTable)
    val teamId = reference("teamId", TeamTable)
    val alliance = enumerationByName<Alliance>("alliance", 255)
}

data class ParticipantNode(val id: Int, val alliance: Alliance) :
    Node<Tree<Participant>, Participant> {

    companion object {
        fun from(participantRow: ResultRow): ParticipantNode {
            return ParticipantNode(
                participantRow[ParticipantTable.id].value,
                participantRow[ParticipantTable.alliance],
            )
        }
    }

    override suspend fun parent(): Parent<Tree<Participant>, Participant> {
        val match = SQLDatabase.getMatchByParticipant(this).getOrNull()!!

        return ParticipantParent(this, match)
    }

    override suspend fun branch(): Branch<Tree<Participant>, Participant> {
        val team = SQLDatabase.getTeamByParticipant(this).getOrNull()!!
        val metrics = SQLDatabase.getMetricsByParticipant(this).getOrNull()!!

        return ParticipantBranch(this, team, metrics)
    }

    override fun tree(): Tree<Participant> {
        return ParticipantTree(this, null, emptyList())
    }
}

data class ParticipantParent(val participant: ParticipantNode, val match: MatchNode) :
    Parent<Tree<Participant>, Participant> {
    override suspend fun branch(): Branch<Tree<Participant>, Participant> {
        return participant.branch()
    }

    override fun tree(): Tree<Participant> {
        return participant.tree()
    }
}

data class ParticipantBranch(
    val participant: ParticipantNode,
    val team: Node<Tree<Team>, Team>,
    val metrics: List<MetricNode>
) :
    Branch<Tree<Participant>, Participant> {
    override suspend fun parent(): Parent<Tree<Participant>, Participant> {
        return participant.parent()
    }

    override suspend fun tree(): Tree<Participant> {
        val team = team.branch()
        val metrics = metrics.map { it.branch() }

        return ParticipantTree(participant, team, metrics)
    }
}

data class ParticipantTree(
    val participant: ParticipantNode,
    val team: Branch<Tree<Team>, Team>?,
    val metrics: List<Branch<Tree<Metric>, Metric>>
) :
    Tree<Participant> {
    override fun leaf(): Participant {
        return Participant(participant.alliance, null, emptyList())
    }

    override suspend fun leaves(): Participant {
        val team = team?.tree()?.leaf()
        val metrics = metrics.map { metric -> metric.tree().leaf() }

        return Participant(participant.alliance, team, metrics)
    }

    override suspend fun subtree(): Participant {
        val team = null // TODO
        val metrics = metrics.map { metric -> metric.tree().subtree() }

        return Participant(participant.alliance, team, metrics)
    }

    override suspend fun subtree(depth: Int): Participant {
        if (depth == 0) return leaf()
        if (depth == 1) return leaves()

        val team = team?.tree()?.subtree(depth - 1)
        val metrics = metrics.map { metric -> metric.tree().subtree(depth - 1) }

        return Participant(participant.alliance, team, metrics)
    }
}

