package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.Alliance
import io.github.haydenheroux.scouting.models.interfaces.Node
import io.github.haydenheroux.scouting.models.interfaces.Parent
import io.github.haydenheroux.scouting.models.interfaces.Subtree
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

data class ParticipantNode(val id: Int, val alliance: Alliance) : Node<ParticipantTree> {

    companion object {
        fun from(participantRow: ResultRow): ParticipantNode {
            return ParticipantNode(
                participantRow[ParticipantTable.id].value,
                participantRow[ParticipantTable.alliance]
            )
        }
    }

    override suspend fun parent(): Parent<ParticipantTree> {
        val match = db.getMatchByParticipant(this)

        return ParticipantParent(this, match)
    }

    override suspend fun subtree(): Subtree<ParticipantTree> {
        val metrics = db.getMetricsByParticipant(this)

        return ParticipantSubtree(this, metrics)
    }

    override fun tree(): ParticipantTree {
        return ParticipantTree(this, emptyList())
    }
}

data class ParticipantParent(val participant: ParticipantNode, val match: MatchNode) : Parent<ParticipantTree> {
    override suspend fun subtree(): Subtree<ParticipantTree> {
        return participant.subtree()
    }

    override fun tree(): ParticipantTree {
        return participant.tree()
    }
}

data class ParticipantSubtree(val participant: ParticipantNode, val metrics: List<MetricNode>) :
    Subtree<ParticipantTree> {
    override suspend fun parent(): Parent<ParticipantTree> {
        return participant.parent()
    }

    override suspend fun tree(): ParticipantTree {
        val metrics = metrics.map { it.subtree() }

        return ParticipantTree(participant, metrics)
    }
}

data class ParticipantTree(val participant: ParticipantNode, val metrics: List<Subtree<MetricTree>>) {
    fun noChildren(): Participant {
        return Participant(participant.alliance, emptyList())
    }

    suspend fun children(): Participant {
        val metrics = metrics.map { metric -> metric.tree().noChildren() }

        return Participant(participant.alliance, metrics)
    }

    suspend fun subChildren(): Participant {
        val metrics = metrics.map { metric -> metric.tree().subChildren() }

        return Participant(participant.alliance, metrics)
    }
}

@Serializable
data class Participant(val alliance: Alliance, val metrics: List<MetricDTO>)

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
