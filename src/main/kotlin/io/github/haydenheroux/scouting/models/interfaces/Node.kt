package io.github.haydenheroux.scouting.models.interfaces

interface Node<A : Tree<B>, B> {
    suspend fun parent(): Parent<A, B>?

    suspend fun subtree(): Subtree<A, B>

    fun tree(): A
}