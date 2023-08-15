package io.github.haydenheroux.scouting.models.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** The region of an FRC team or event. */
@Serializable
enum class Region {
    @SerialName("ne")
    NEW_ENGLAND,

    @SerialName("isr")
    ISRAEL,

    @SerialName("fim")
    MICHIGAN,

    @SerialName("fit")
    TEXAS,

    @SerialName("fin")
    INDIANA,

    @SerialName("chs")
    CHESAPEAKE,

    @SerialName("fma")
    MID_ATLANTIC,

    @SerialName("pch")
    PEACHTREE,

    @SerialName("ont")
    ONTARIO,

    @SerialName("other")
    OTHER
}

/** Returns the correct region for a given string. */
val regionOf = mapOf(
    "ne" to Region.NEW_ENGLAND,
    "isr" to Region.ISRAEL,
    "fim" to Region.MICHIGAN,
    "fit" to Region.TEXAS,
    "fin" to Region.INDIANA,
    "chs" to Region.CHESAPEAKE,
    "fma" to Region.MID_ATLANTIC,
    "pch" to Region.PEACHTREE,
    "ont" to Region.ONTARIO,
    "other" to Region.OTHER
)
