package io.github.haydenheroux.scouting.models.interfaces

interface Subtree<T> {
    suspend fun parent(): Parent<T>?

    suspend fun tree(): T
}