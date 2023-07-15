package io.github.haydenheroux.scouting.database

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
