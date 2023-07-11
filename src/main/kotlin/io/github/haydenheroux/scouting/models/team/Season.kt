package io.github.haydenheroux.scouting.models.team

import io.github.haydenheroux.scouting.models.event.Event

/**
 * A season is one year of competition for an FRC team.
 *
 * Each season takes place during a single year. During each season, an
 * FRC team must build a number of robots, and participate in a number of
 * events.
 *
 * @property team the team that the season belongs to.
 * @property year the year that the season takes place in.
 * @property robots the robots that the team built during the season.
 * @property events the events that the team participated in during the
 *     season.
 * @see Robot
 * @see Event
 */
data class Season(val team: Team, val year: Int, val robots: MutableList<Robot>, val events: MutableList<Event>)
