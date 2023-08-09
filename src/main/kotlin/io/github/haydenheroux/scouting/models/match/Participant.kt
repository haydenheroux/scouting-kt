package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.Alliance
import io.github.haydenheroux.scouting.models.interfaces.Data
import io.github.haydenheroux.scouting.models.interfaces.Parented
import io.github.haydenheroux.scouting.models.interfaces.Reference
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

data class ParticipantData(val participantId: Int, val alliance: Alliance) : Data<Participant> {

    companion object {
        fun from(participantRow: ResultRow): ParticipantData {
            return ParticipantData(
                participantRow[ParticipantTable.id].value,
                participantRow[ParticipantTable.alliance]
            )
        }
    }

    override suspend fun parent(): Parented<Participant> {
        val matchData = db.getMatchByParticipant(this)

        return ParentedParticipant(this, matchData)
    }

    override suspend fun reference(): Reference<Participant> {
        val metricData = db.getMetricsByParticipant(this)

        return ParticipantReference(this, metricData)
    }

    override fun data(): Participant {
        return Participant(this, emptyList())
    }
}

data class ParentedParticipant(val participantData: ParticipantData, val matchData: MatchData) : Parented<Participant> {
    override suspend fun reference(): Reference<Participant> {
        return participantData.reference()
    }

    override fun data(): Participant {
        return participantData.data()
    }
}

data class ParticipantReference(val participantData: ParticipantData, val metricData: List<MetricData>) :
    Reference<Participant> {
    override suspend fun parent(): Parented<Participant> {
        return participantData.parent()
    }

    override suspend fun dereference(): Participant {
        val metricReferences = metricData.map { it.reference() }

        return Participant(participantData, metricReferences)
    }
}

data class Participant(val participantData: ParticipantData, val metricReferences: List<Reference<Metric>>)

@Serializable
data class ParticipantDTO(val alliance: Alliance, val metrics: List<MetricDTO>)

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
