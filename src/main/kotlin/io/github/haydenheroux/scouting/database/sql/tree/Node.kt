package io.github.haydenheroux.scouting.database.sql.tree

interface Node<A : Tree<B>, B> {
    suspend fun branch(): Branch<A, B>

    fun tree(): A
}