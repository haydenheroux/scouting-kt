package io.github.haydenheroux.scouting.models

import io.github.haydenheroux.scouting.models.enums.Region
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val code: String,
    val name: String,
    val region: Region,
    val year: Int,
    val week: Int,
    val matches: List<Match>
)

data class EventQuery(val code: String)

fun eventQueryOf(event: Event): EventQuery {
    return EventQuery(event.code)
}

fun eventQueryOf(parameters: Parameters): Result<EventQuery> {
    val code = parameters["event"] ?: return Result.failure(Exception("Missing `event` in parameters"))

    return Result.success(EventQuery(code))
}