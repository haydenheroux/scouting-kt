package io.github.haydenheroux.scouting.database.sql.tree

interface Node<A : Branch<B>, B> {
    suspend fun branch(): A

    fun root(): A
}