package io.github.haydenheroux.scouting.models.interfaces

interface Parent<A : Tree<B>, B> {
    suspend fun branch(): Branch<A, B>

    fun tree(): A
}