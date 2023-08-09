package io.github.haydenheroux.scouting.models.event

import io.github.haydenheroux.scouting.models.enums.Region
import io.github.haydenheroux.scouting.models.enums.regionOf
import io.ktor.http.*
import org.jetbrains.exposed.dao.id.IntIdTable

object EventTable : IntIdTable() {
    val name = varchar("name", 255)
    val region = enumerationByName<Region>("region", 255)
    val year = integer("year")
    val week = integer("week")
}
data class EventQuery(val name: String, val region: Region, val year: Int, val week: Int)

fun Parameters.eventQuery(): Result<EventQuery> {
    val name = this["event"] ?: return Result.failure(Exception("Missing `event` in parameters"))
    val region = regionOf[this["region"]] ?: return Result.failure(Exception("Missing `region` in parameters"))
    val year = this["year"] ?: return Result.failure(Exception("Missing `year` in parameters"))
    val week = this["week"] ?: return Result.failure(Exception("Missing `week` in parameters"))

    return Result.success(EventQuery(name, region, year.toInt(), week.toInt()))
}
