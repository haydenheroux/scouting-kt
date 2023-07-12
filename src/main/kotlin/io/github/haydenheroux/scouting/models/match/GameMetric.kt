package io.github.haydenheroux.scouting.models.match

import kotlinx.serialization.Serializable

@Serializable
data class GameMetric(val key: String, val value: String)
