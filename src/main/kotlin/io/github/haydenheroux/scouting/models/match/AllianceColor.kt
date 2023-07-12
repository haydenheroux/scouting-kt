package io.github.haydenheroux.scouting.models.match

/**
 * A color of an alliance.
 *
 * Each alliance must be red alliance, blue alliance, or other alliance.
 * Other alliances are typically practice alliances or non-competition
 * matches.
 */
enum class AllianceColor {
    RED,
    BLUE,
    OTHER
}