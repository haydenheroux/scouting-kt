package io.github.haydenheroux.scouting.database.sql.tree

interface Parent<A : Tree<B>, B> {
    suspend fun branch(): Branch<A, B>

    fun tree(): A
}