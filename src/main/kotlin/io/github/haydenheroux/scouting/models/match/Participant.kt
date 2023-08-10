package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.Alliance
import io.github.haydenheroux.scouting.models.interfaces.Node
import io.github.haydenheroux.scouting.models.interfaces.Parent
import io.github.haydenheroux.scouting.models.interfaces.Subtree
import io.github.haydenheroux.scouting.models.interfaces.Tree
import io.github.haydenheroux.scouting.models.team.RobotQuery
import io.github.haydenheroux.scouting.models.team.RobotTable
import io.github.haydenheroux.scouting.models.team.robotQuery
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object ParticipantTable : IntIdTable() {
    val matchId = reference("matchId", MatchTable)
    val robotId = reference("robotId", RobotTable)
    val alliance = enumerationByName<Alliance>("alliance", 255)
}

data class ParticipantNode(val id: Int, val alliance: Alliance) : Node<Tree<Participant>, Participant> {

    companion object {
        fun from(participantRow: ResultRow): ParticipantNode {
            return ParticipantNode(
                participantRow[ParticipantTable.id].value,
                participantRow[ParticipantTable.alliance]
            )
        }
    }

    override suspend fun parent(): Parent<Tree<Participant>, Participant> {
        val match = db.getMatchByParticipant(this)

        return ParticipantParent(this, match)
    }

    override suspend fun subtree(): Subtree<Tree<Participant>, Participant> {
        val metrics = db.getMetricsByParticipant(this)

        return ParticipantSubtree(this, metrics)
    }

    override fun tree(): Tree<Participant> {
        return ParticipantTree(this, emptyList())
    }
}

data class ParticipantParent(val participant: ParticipantNode, val match: MatchNode) :
    Parent<Tree<Participant>, Participant> {
    override suspend fun subtree(): Subtree<Tree<Participant>, Participant> {
        return participant.subtree()
    }

    override fun tree(): Tree<Participant> {
        return participant.tree()
    }
}

data class ParticipantSubtree(val participant: ParticipantNode, val metrics: List<MetricNode>) :
    Subtree<Tree<Participant>, Participant> {
    override suspend fun parent(): Parent<Tree<Participant>, Participant> {
        return participant.parent()
    }

    override suspend fun tree(): Tree<Participant> {
        val metrics = metrics.map { it.subtree() }

        return ParticipantTree(participant, metrics)
    }
}

data class ParticipantTree(val participant: ParticipantNode, val metrics: List<Subtree<Tree<Metric>, Metric>>) :
    Tree<Participant> {
    override fun leaf(): Participant {
        return Participant(participant.alliance, emptyList())
    }

    override suspend fun leaves(): Participant {
        val metrics = metrics.map { metric -> metric.tree().leaf() }

        return Participant(participant.alliance, metrics)
    }

    override suspend fun subtree(): Participant {
        val metrics = metrics.map { metric -> metric.tree().subtree() }

        return Participant(participant.alliance, metrics)
    }

    override suspend fun subtree(depth: Int): Participant {
        if (depth == 0) return leaf()
        if (depth == 1) return leaves()

        val metrics = metrics.map { metric -> metric.tree().subtree(depth - 1) }

        return Participant(participant.alliance, metrics)
    }
}

@Serializable
data class Participant(val alliance: Alliance, val metrics: List<Metric>)

data class ParticipantQuery(val match: MatchQuery, val robot: RobotQuery)

fun participantQueryOf(matchQuery: MatchQuery, robotQuery: RobotQuery): ParticipantQuery {
    return ParticipantQuery(matchQuery, robotQuery)
}

fun Parameters.participantQuery(): Result<ParticipantQuery> {
    val match = this.matchQuery()

    if (match.isFailure) {
        return Result.failure(match.exceptionOrNull()!!)
    }

    val robot = this.robotQuery()

    if (robot.isFailure) {
        return Result.failure(robot.exceptionOrNull()!!)
    }

    return Result.success(ParticipantQuery(match.getOrNull()!!, robot.getOrNull()!!))
}
