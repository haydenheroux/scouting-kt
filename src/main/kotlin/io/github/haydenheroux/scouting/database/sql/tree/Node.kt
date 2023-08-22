package io.github.haydenheroux.scouting.database.sql.tree

import io.github.haydenheroux.scouting.database.sql.excludes.Exclude

interface Node<A : Tree<B>, B> {
    suspend fun tree(parent: Boolean, excludes: List<Exclude>): A

    fun leaf(): B
}