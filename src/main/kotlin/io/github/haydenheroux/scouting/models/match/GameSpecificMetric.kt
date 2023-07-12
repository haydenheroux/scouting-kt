package io.github.haydenheroux.scouting.models.match

import kotlinx.serialization.Serializable

@Serializable
data class GameSpecificMetric(val key: String, val value: String)
