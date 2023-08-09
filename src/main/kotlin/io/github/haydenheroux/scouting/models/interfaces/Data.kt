package io.github.haydenheroux.scouting.models.interfaces

interface Data<T> {
    fun parent(): Parented<T>

    fun reference(): Reference<T>

    fun data(): T
}