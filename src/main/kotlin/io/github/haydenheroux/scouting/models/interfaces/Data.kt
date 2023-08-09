package io.github.haydenheroux.scouting.models.interfaces

interface Data<T> {
    suspend fun parent(): Parented<T>?

    suspend fun reference(): Reference<T>

    fun data(): T
}