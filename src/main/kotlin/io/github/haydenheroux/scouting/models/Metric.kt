package io.github.haydenheroux.scouting.models

import kotlinx.serialization.Serializable

@Serializable
data class Metric(val key: String, val value: String)