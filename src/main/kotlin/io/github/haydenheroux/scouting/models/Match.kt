package io.github.haydenheroux.scouting.models

data class Match(val event: Event, val number: Int, val metrics: MutableMap<Robot, GameSpecificMetrics>)
