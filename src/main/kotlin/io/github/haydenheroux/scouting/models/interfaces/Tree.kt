package io.github.haydenheroux.scouting.models.interfaces

interface Tree<T> {
    fun leaf(): T

    suspend fun leaves(): T

    suspend fun subtree(): T

    suspend fun subtree(depth: Int): T
}