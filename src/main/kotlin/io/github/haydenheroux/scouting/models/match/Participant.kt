package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.Database.query
import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.Alliance
import io.github.haydenheroux.scouting.models.team.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

object ParticipantTable : IntIdTable() {
    val matchId = reference("matchId", MatchTable)
    val robotId = reference("robotId", RobotTable)
    val alliance = enumerationByName<Alliance>("alliance", 255)
}

@Serializable
data class ParticipantProperties(
    val alliance: Alliance,
)

fun ResultRow.participantProperties(): ParticipantProperties {
    val alliance = this[ParticipantTable.alliance]

    return ParticipantProperties(alliance)
}

data class ParticipantReference(
    val participantId: Int,
    val matchReference: MatchReference,
    val robotReference: RobotReference,
    val alliance: Alliance,
)

suspend fun ResultRow.participantReference(): ParticipantReference {
    val participantId = this[ParticipantTable.id].value
    val properties = this.participantProperties()

    val matchId = this[ParticipantTable.matchId].value
    val matchReference = db.getMatch(matchId)

    val robotId = this[ParticipantTable.robotId].value
    val robotReference = db.getRobot(robotId)

    return ParticipantReference(participantId, matchReference, robotReference, properties.alliance)
}

suspend fun ParticipantReference.dereference(children: Boolean): Participant {
    val metrics = if (children) db.getMetrics(this).map { it.dereference(true) } else emptyList()
    return Participant(alliance, metrics)
}

@Serializable
data class Participant(
    val alliance: Alliance,
    val metrics: List<Metric>
)

data class ParticipantQuery(val match: MatchQuery, val robot: RobotQuery)

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
