package io.github.haydenheroux.scouting.models

data class Event(
    val name: String,
    val location: String,
    val year: Int,
    val week: Int,
    val matches: MutableMap<Int, Match>
)
