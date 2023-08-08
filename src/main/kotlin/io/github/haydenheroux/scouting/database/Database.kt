package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.models.event.EventTable
import io.github.haydenheroux.scouting.models.match.MatchTable
import io.github.haydenheroux.scouting.models.match.MetricTable
import io.github.haydenheroux.scouting.models.match.ParticipantTable
import io.github.haydenheroux.scouting.models.team.RobotTable
import io.github.haydenheroux.scouting.models.team.SeasonEventTable
import io.github.haydenheroux.scouting.models.team.SeasonTable
import io.github.haydenheroux.scouting.models.team.TeamTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object Database {
    fun init() {
        val url = "jdbc:sqlite:./build/db"
        val driver = "org.sqlite.JDBC"
        val database = Database.connect(url, driver)
        transaction(database) {
            SchemaUtils.create(
                TeamTable,
                SeasonTable,
                RobotTable,
                ParticipantTable,
                MetricTable,
                MatchTable,
                EventTable,
                SeasonEventTable
            )
        }
    }

    suspend fun <T> query(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }
}
