package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.models.enums.Alliance
import io.github.haydenheroux.scouting.models.team.RobotQuery
import io.github.haydenheroux.scouting.models.team.RobotTable
import io.github.haydenheroux.scouting.models.team.robotQuery
import io.ktor.http.*
import org.jetbrains.exposed.dao.id.IntIdTable

object ParticipantTable : IntIdTable() {
    val matchId = reference("matchId", MatchTable)
    val robotId = reference("robotId", RobotTable)
    val alliance = enumerationByName<Alliance>("alliance", 255)
}
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
