package io.github.haydenheroux.scouting.database.sql.tree

interface Tree<T> {
    suspend fun leaves(): T

    suspend fun subtree(): T

    suspend fun subtree(depth: Int): T
}