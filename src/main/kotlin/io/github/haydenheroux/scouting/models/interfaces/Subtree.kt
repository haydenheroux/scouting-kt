package io.github.haydenheroux.scouting.models.interfaces

interface Subtree<A : Tree<B>, B> {
    suspend fun parent(): Parent<A, B>?

    suspend fun tree(): A
}