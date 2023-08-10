package io.github.haydenheroux.scouting.models.interfaces

interface Node<T> {
    suspend fun parent(): Parent<T>?

    suspend fun subtree(): Subtree<T>

    fun tree(): T
}