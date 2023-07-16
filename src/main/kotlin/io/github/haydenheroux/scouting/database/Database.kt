package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.models.event.Events
import io.github.haydenheroux.scouting.models.event.SeasonEvents
import io.github.haydenheroux.scouting.models.match.GameMetrics
import io.github.haydenheroux.scouting.models.match.Matches
import io.github.haydenheroux.scouting.models.match.Metrics
import io.github.haydenheroux.scouting.models.team.Robots
import io.github.haydenheroux.scouting.models.team.Seasons
import io.github.haydenheroux.scouting.models.team.Teams
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
            SchemaUtils.create(Teams, Seasons, Robots, Metrics, GameMetrics, Matches, Events, SeasonEvents)
        }
    }

    suspend fun <T> query(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }
}
