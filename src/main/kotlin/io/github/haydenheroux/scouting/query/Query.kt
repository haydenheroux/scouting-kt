package io.github.haydenheroux.scouting.query

import io.github.haydenheroux.scouting.models.enums.Region
import io.github.haydenheroux.scouting.models.enums.regionOf
import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.match.Match
import io.github.haydenheroux.scouting.models.team.Robot
import io.github.haydenheroux.scouting.models.team.Season
import io.github.haydenheroux.scouting.models.team.Team
import io.ktor.http.*

data class TeamQuery(val teamNumber: Int)

fun teamQueryFromTeam(team: Team): TeamQuery {
    return TeamQuery(team.number)
}

fun teamQueryFromParameters(parameters: Parameters): TeamQuery {
    val teamNumber = parameters["team"]!!.toInt()

    return TeamQuery(teamNumber)
}

data class SeasonQuery(val team: TeamQuery, val year: Int)

fun seasonQueryFromSeason(teamQuery: TeamQuery, season: Season): SeasonQuery {
    return SeasonQuery(teamQuery, season.year)
}

fun seasonQueryFromParameters(parameters: Parameters): SeasonQuery {
    val team = teamQueryFromParameters(parameters)
    val year = parameters["year"]!!.toInt()

    return SeasonQuery(team, year)
}

data class RobotQuery(val season: SeasonQuery, val robotName: String)

fun robotQueryFromRobot(seasonQuery: SeasonQuery, robot: Robot): RobotQuery {
    return RobotQuery(seasonQuery, robot.name)
}

fun robotQueryFromParameters(parameters: Parameters): RobotQuery {
    val season = seasonQueryFromParameters(parameters)
    val robotName = parameters["robot"]!!

    return RobotQuery(season, robotName)
}

data class EventQuery(val eventName: String, val region: Region, val year: Int, val week: Int)

fun eventQueryFromEvent(event: Event): EventQuery {
    return EventQuery(event.name, event.region, event.year, event.week)
}

fun eventQueryFromParameters(parameters: Parameters): EventQuery {
    val eventName = parameters["event"]!!
    val region = regionOf[parameters["region"]]!!
    val year = parameters["year"]!!.toInt()
    val week = parameters["week"]!!.toInt()

    return EventQuery(eventName, region, year, week)
}

data class MatchQuery(val event: EventQuery, val matchNumber: Int)

fun matchQueryFromMatch(eventQuery: EventQuery, match: Match): MatchQuery {
    return MatchQuery(eventQuery, match.number)
}

fun matchQueryFromParameters(parameters: Parameters): MatchQuery {
    val event = eventQueryFromParameters(parameters)
    val matchNumber = parameters["match"]!!.toInt()

    return MatchQuery(event, matchNumber)
}

data class MetricQuery(val match: MatchQuery, val robot: RobotQuery)

fun metricQueryFromParameters(parameters: Parameters): MetricQuery {
    val match = matchQueryFromParameters(parameters)
    val robot = robotQueryFromParameters(parameters)

    return MetricQuery(match, robot)
}
