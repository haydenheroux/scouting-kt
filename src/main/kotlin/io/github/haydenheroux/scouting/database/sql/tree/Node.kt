package io.github.haydenheroux.scouting.database.sql.tree

interface Node<A : Tree<B>, B> {
    suspend fun tree(parent: Boolean): A

    fun leaf(): B
}