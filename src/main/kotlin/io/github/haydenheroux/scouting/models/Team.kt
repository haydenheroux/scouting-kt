package io.github.haydenheroux.scouting.models

data class Team(val number: Int, val name: String, val location: String, val seasons: MutableMap<Int, Season>)
