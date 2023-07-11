package io.github.haydenheroux.scouting.models

data class Season(val team: Team, val year: Int, val robot: Robot, val events: MutableList<Event>)
