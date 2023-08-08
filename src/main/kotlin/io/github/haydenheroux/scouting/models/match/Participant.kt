package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.Database.query
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
    val alliance: Alliance,
    val matchReference: MatchReference?,
    val robotReference: RobotReference?,
    val gameMetricReferences: List<GameMetricReference>
)

suspend fun ResultRow.asParticipantReference(noParent: Boolean, noChildren: Boolean): ParticipantReference {
    val properties = this.participantProperties()

    val matchId = this[ParticipantTable.matchId]
    val matchReference = if (noParent) null else query {
        MatchTable.select { MatchTable.id eq matchId }.map { it.asMatchReference(false, true) }.single()
    }
    val robotId = this[ParticipantTable.robotId]
    val robotReference = if (noParent) null else query {
        RobotTable.select { RobotTable.id eq robotId }.map { it.asRobotReference(false) }.single()
    }

    val participantId = this[ParticipantTable.id]
    val gameMetricReferences = if (noChildren) listOf() else query {
        GameMetricTable.select { GameMetricTable.participantId eq participantId }
            .map { it.asGameMetricReference(false) }
    }

    return ParticipantReference(properties.alliance, matchReference, robotReference, gameMetricReferences)
}

fun ParticipantReference.dereference(): Participant {
    // TODO Handle robotReference == null
    val robot = robotReference!!.dereference()
    val gameMetrics = gameMetricReferences.map { it.dereference() }
    return Participant(robot, alliance, gameMetrics)
}

@Serializable
data class Participant(
    val robot: Robot,
    val alliance: Alliance,
    val gameMetrics: List<GameMetric>
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
