package io.github.haydenheroux.scouting.models.interfaces

interface Reference<T> {
    fun parent(): Parented<T>

    fun dereference(): T
}