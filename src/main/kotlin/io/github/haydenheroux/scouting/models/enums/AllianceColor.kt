package io.github.haydenheroux.scouting.models.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A color of an alliance.
 *
 * Each alliance must be red alliance, blue alliance, or other alliance.
 * Other alliances are typically practice alliances or non-competition
 * matches.
 */
@Serializable
enum class AllianceColor {
    @SerialName("red")
    RED,

    @SerialName("blue")
    BLUE,
}

val allianceColorOf = mapOf(
    "red" to AllianceColor.RED,
    "blue" to AllianceColor.BLUE
)