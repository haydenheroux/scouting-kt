package io.github.haydenheroux.scouting.database.sql.tree

interface Tree<A : Branch<B>, B> {
    suspend fun branch(): A
}