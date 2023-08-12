package io.github.haydenheroux.scouting.database.sql.tree

interface Node<A : Tree<B>, B> {
    suspend fun parent(): Parent<A, B>?

    suspend fun branch(): Branch<A, B>

    fun tree(): A
}