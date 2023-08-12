package io.github.haydenheroux.scouting.database.sql.tree

interface Node<A : Branch<B>, B> {
    suspend fun tree(): Tree<A, B>

    fun root(): A
}