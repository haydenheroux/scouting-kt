package io.github.haydenheroux.scouting.models.interfaces

interface Reference<T> {
    suspend fun parent(): Parented<T>?

    suspend fun dereference(): T
}