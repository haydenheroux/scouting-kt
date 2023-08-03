package io.github.haydenheroux.scouting.database

import io.github.haydenheroux.scouting.models.enums.Region
import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.match.GameMetric
import io.github.haydenheroux.scouting.models.match.Match
import io.github.haydenheroux.scouting.models.match.Metric
import io.github.haydenheroux.scouting.models.team.Robot
import io.github.haydenheroux.scouting.models.team.Season
import io.github.haydenheroux.scouting.models.team.Team

interface DatabaseInterface {
    /**
     * Gets all teams.
     *
     * @return the list containing all teams.
     */
    suspend fun getTeams(): List<Team>

    /**
     * Gets the team with the specified team number.
     *
     * @param number the number of the team.
     * @return the team with the specified team number.
     */
    suspend fun getTeamByNumber(number: Int): Team

    /**
     * Gets the season in the specified year that belongs to the team with the
     * specified number.
     *
     * @param number the number of the team.
     * @param year the year of the season.
     * @return the season in the specified year that belongs to the team with
     *     the specified number.
     */
    suspend fun getSeasonByNumberYear(number: Int, year: Int): Season

    /**
     * Gets the robot in the specified season with the specified name.
     *
     * @param number the number of the team.
     * @param year the year of the season.
     * @param name the name of the robot.
     * @return the robot in the specified season with the specified name.
     */
    suspend fun getRobotByNumberYearName(number: Int, year: Int, name: String): Robot

    /**
     * Gets all events.
     *
     * @return the list containing all teams.
     */
    suspend fun getEvents(): List<Event>

    /**
     * Gets all stored events with the specified region.
     *
     * @param region the region of the events.
     * @return the list containing events all events in the specified region.
     */
    suspend fun getEventsByRegion(region: Region): List<Event>

    /**
     * Gets the event with the specified properties.
     *
     * @param name the name of the event.
     * @param region the region the event is in.
     * @param year the year of the event.
     * @param week the week of the event.
     * @return the event with the specified properties.
     */
    suspend fun getEventByNameRegionYearWeek(name: String, region: Region, year: Int, week: Int): Event

    /**
     * Gets the match with the specified properties.
     *
     * @param name the name of the event.
     * @param region the region the event is in.
     * @param year the year of the event.
     * @param week the week of the event.
     * @param number the number of the match.
     * @return the match with the specified properties.
     */
    suspend fun getMatchByNameRegionYearWeekNumber(
        name: String,
        region: Region,
        year: Int,
        week: Int,
        number: Int
    ): Match

    suspend fun insertTeam(team: Team)
    suspend fun insertSeason(season: Season)
    suspend fun insertRobot(robot: Robot)
    suspend fun insertEvent(event: Event)
    suspend fun insertSeasonEvent(event: Event, season: Season)
    suspend fun insertMatch(match: Match)
    suspend fun insertMetric(metric: Metric)
    suspend fun insertGameMetric(gameMetric: GameMetric)
}
