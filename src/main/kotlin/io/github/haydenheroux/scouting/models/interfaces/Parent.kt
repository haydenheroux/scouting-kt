package io.github.haydenheroux.scouting.models.interfaces

interface Parent<A : Tree<B>, B> {
    suspend fun subtree(): Subtree<A, B>

    fun tree(): A
}