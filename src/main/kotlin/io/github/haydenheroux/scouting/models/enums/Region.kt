package io.github.haydenheroux.scouting.models.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** The region of an FRC team or event. */
@Serializable
enum class Region {
    @SerialName("ne")
    NEW_ENGLAND
}

/** Returns the correct region for a given string. */
val regionOf: Map<String, Region> = mapOf("ne" to Region.NEW_ENGLAND)
