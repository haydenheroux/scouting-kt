package io.github.haydenheroux.scouting.database.sql.tree

interface Branch<T> {
    fun leaf(): T

    suspend fun leaves(): T

    suspend fun subbranch(): T

    suspend fun subbranch(depth: Int): T
}