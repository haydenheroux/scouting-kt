package io.github.haydenheroux.scouting.models.interfaces

interface Parent<T> {
    suspend fun subtree(): Subtree<T>

    fun tree(): T
}