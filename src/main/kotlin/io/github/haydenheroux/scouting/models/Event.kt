package io.github.haydenheroux.scouting.models

/**
 * An event is an FRC competition event.
 *
 * Each event has some properties that identify it. For example, the name
 * and location are identifiable properties. Additionally, each event has
 * the year and week that the event occurred. Weeks are measured according
 * to the FRC schedule, beginning at zero. Finally, each event has matches
 * that are played at the event.
 *
 * @property name the name of the event.
 * @property location the location of the event.
 * @property year the year that the event occurred.
 * @property week the week that the event occurred.
 * @property matches the matches that were played at the event.
 * @see Match
 */
data class Event(
    val name: String,
    val location: String,
    val year: Int,
    val week: Int,
    val matches: MutableList<Match>
)
