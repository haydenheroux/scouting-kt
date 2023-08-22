package io.github.haydenheroux.scouting.database.sql.tree

import io.github.haydenheroux.scouting.database.sql.excludes.Exclude

interface Tree<T> {
    suspend fun subtree(): T

    suspend fun subtree(depth: Int, excludes: List<Exclude>): T
}