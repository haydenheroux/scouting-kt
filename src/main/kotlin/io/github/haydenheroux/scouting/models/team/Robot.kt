package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.interfaces.Data
import io.github.haydenheroux.scouting.models.interfaces.Parented
import io.github.haydenheroux.scouting.models.interfaces.Reference
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object RobotTable : IntIdTable() {
    val seasonId = reference("seasonId", SeasonTable)
    val name = varchar("name", 255)
}

data class RobotData(val robotId: Int, val name: String) : Data<Robot> {

    companion object {
        fun from(robotRow: ResultRow): RobotData {
            return RobotData(
                robotRow[RobotTable.id].value,
                robotRow[RobotTable.name]
            )
        }
    }

    override suspend fun parent(): Parented<Robot> {
        val seasonData = db.getSeasonByRobot(this)

        return ParentedRobot(this, seasonData)
    }

    override suspend fun reference(): Reference<Robot> {
        return RobotReference(this)
    }

    override fun data(): Robot {
        return Robot(this)
    }
}

data class ParentedRobot(val robotData: RobotData, val seasonData: SeasonData) : Parented<Robot> {
    override suspend fun reference(): Reference<Robot> {
        return robotData.reference()
    }

    override fun data(): Robot {
        return robotData.data()
    }
}

data class RobotReference(val robotData: RobotData) : Reference<Robot> {
    override suspend fun parent(): Parented<Robot> {
        return robotData.parent()
    }

    override suspend fun dereference(): Robot {
        return Robot(robotData)
    }
}

data class Robot(val robotData: RobotData) {
    fun noChildren(): RobotDTO {
        return RobotDTO(robotData.name)
    }

    fun children(): RobotDTO {
        return RobotDTO(robotData.name)
    }

    fun subChildren(): RobotDTO {
        return RobotDTO(robotData.name)
    }
}

@Serializable
data class RobotDTO(val name: String)

data class RobotQuery(val name: String, val season: SeasonQuery)

fun robotQueryOf(robotDTO: RobotDTO, seasonQuery: SeasonQuery): RobotQuery {
    return RobotQuery(robotDTO.name, seasonQuery)
}

fun Parameters.robotQuery(): Result<RobotQuery> {
    val name = this["robot"] ?: return Result.failure(Exception("Missing `robot` in parameters"))

    val season = this.seasonQuery()

    if (season.isFailure) {
        return Result.failure(season.exceptionOrNull()!!)
    }

    return Result.success(RobotQuery(name, season.getOrNull()!!))
}
