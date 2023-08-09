package io.github.haydenheroux.scouting.models.interfaces

interface Parented<T> {
    fun reference(): Reference<T>

    fun data(): T
}