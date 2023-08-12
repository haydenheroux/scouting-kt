package io.github.haydenheroux.scouting.models

import io.github.haydenheroux.scouting.models.enums.Region
import io.github.haydenheroux.scouting.models.enums.regionOf
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Event(val name: String, val region: Region, val year: Int, val week: Int, val matches: List<Match>)

data class EventQuery(val name: String, val region: Region, val year: Int, val week: Int)

fun eventQueryOf(event: Event): EventQuery {
    return EventQuery(event.name, event.region, event.year, event.week)
}

fun eventQueryOf(parameters: Parameters): Result<EventQuery> {
    val name = parameters["event"] ?: return Result.failure(Exception("Missing `event` in parameters"))
    val region = regionOf[parameters["region"]] ?: return Result.failure(Exception("Missing `region` in parameters"))
    val year = parameters["year"] ?: return Result.failure(Exception("Missing `year` in parameters"))
    val week = parameters["week"] ?: return Result.failure(Exception("Missing `week` in parameters"))

    return Result.success(EventQuery(name, region, year.toInt(), week.toInt()))
}