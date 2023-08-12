package io.github.haydenheroux.scouting.database.sql.tree

interface Branch<A : Tree<B>, B> {
    suspend fun parent(): Parent<A, B>?

    suspend fun tree(): A
}