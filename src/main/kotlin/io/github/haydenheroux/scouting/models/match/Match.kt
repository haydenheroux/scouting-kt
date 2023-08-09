package io.github.haydenheroux.scouting.models.match

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.MatchType
import io.github.haydenheroux.scouting.models.event.EventData
import io.github.haydenheroux.scouting.models.event.EventQuery
import io.github.haydenheroux.scouting.models.event.EventTable
import io.github.haydenheroux.scouting.models.event.eventQuery
import io.github.haydenheroux.scouting.models.interfaces.Data
import io.github.haydenheroux.scouting.models.interfaces.Parented
import io.github.haydenheroux.scouting.models.interfaces.Reference
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object MatchTable : IntIdTable() {
    val eventId = reference("eventId", EventTable)
    val number = integer("number")
    val type = enumerationByName<MatchType>("type", 255)
}

data class MatchData(val matchId: Int, val number: Int, val type: MatchType) : Data<Match> {

    companion object {
        fun from(matchRow: ResultRow): MatchData {
            return MatchData(
                matchRow[MatchTable.id].value,
                matchRow[MatchTable.number],
                matchRow[MatchTable.type]
            )
        }
    }

    override suspend fun parent(): Parented<Match> {
        val eventData = db.getEventByMatch(this)

        return ParentedMatch(this, eventData)
    }

    override suspend fun reference(): Reference<Match> {
        val participantData = db.getParticipantsByMatch(this)

        return MatchReference(this, participantData)
    }

    override fun data(): Match {
        return Match(this, emptyList())
    }
}

data class ParentedMatch(val matchData: MatchData, val eventData: EventData) : Parented<Match> {
    override suspend fun reference(): Reference<Match> {
        return matchData.reference()
    }

    override fun data(): Match {
        return matchData.data()
    }
}

data class MatchReference(val matchData: MatchData, val participantData: List<ParticipantData>) : Reference<Match> {
    override suspend fun parent(): Parented<Match> {
        return matchData.parent()
    }

    override suspend fun dereference(): Match {
        val participantReferences = participantData.map { it.reference() }

        return Match(matchData, participantReferences)
    }
}

data class Match(val matchData: MatchData, val participantReferences: List<Reference<Participant>>)

@Serializable
data class MatchDTO(val number: Int, val type: MatchType, val participants: List<ParticipantDTO>)

data class MatchQuery(val number: Int, val event: EventQuery)

fun matchQueryOf(matchDTO: MatchDTO, eventQuery: EventQuery): MatchQuery {
    return MatchQuery(matchDTO.number, eventQuery)
}

fun Parameters.matchQuery(): Result<MatchQuery> {
    val number = this["match"] ?: return Result.failure(Exception("Missing `number` in parameters"))

    val event = this.eventQuery()

    if (event.isFailure) {
        return Result.failure(event.exceptionOrNull()!!)
    }

    return Result.success(MatchQuery(number.toInt(), event.getOrNull()!!))
}
