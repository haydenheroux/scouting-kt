package io.github.haydenheroux.scouting.models.interfaces

interface Parented<T> {
    suspend fun reference(): Reference<T>

    fun data(): T
}