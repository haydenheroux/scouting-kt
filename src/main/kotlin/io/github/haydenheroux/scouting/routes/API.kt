package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.errors.DatabaseError
import io.github.haydenheroux.scouting.errors.Error
import io.github.haydenheroux.scouting.errors.Success
import io.github.haydenheroux.scouting.errors.getHttpStatusCode
import io.github.haydenheroux.scouting.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.api() {
    route("/api") {
        get("/get-teams") {
            val teamsOrError = SQLDatabase.getTeams()

            if (teamsOrError is Error) {
                call.respond(teamsOrError.error.getHttpStatusCode())
                return@get
            }

            val teams = (teamsOrError as Success).value

            call.respond(teams)
        }

        get("/get-team") {
            val teamQuery = teamQueryOf(call.request.queryParameters).getOrNull()

            if (teamQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val teamOrError = SQLDatabase.getTeam(teamQuery)

            if (teamOrError is Error) {
                call.respond(teamOrError.error.getHttpStatusCode())
                return@get
            }

            val team = (teamOrError as Success).value

            call.respond(team)
        }

        get("/get-season") {
            val seasonQuery = seasonQueryOf(call.request.queryParameters).getOrNull()

            if (seasonQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val seasonOrError = SQLDatabase.getSeason(seasonQuery)

            if (seasonOrError is Error) {
                call.respond(seasonOrError.error.getHttpStatusCode())
                return@get
            }

            val season = (seasonOrError as Success).value

            call.respond(season)
        }

        get("/get-robot") {
            val robotQuery = robotQueryOf(call.request.queryParameters).getOrNull()

            if (robotQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val robotOrError = SQLDatabase.getRobot(robotQuery)

            if (robotOrError is Error) {
                call.respond(robotOrError.error.getHttpStatusCode())
                return@get
            }

            val robot = (robotOrError as Success).value

            call.respond(robot)
        }

        get("/get-events") {
            val eventsOrError = SQLDatabase.getEvents()

            if (eventsOrError is Error) {
                call.respond(eventsOrError.error.getHttpStatusCode())
                return@get
            }

            val events = (eventsOrError as Success).value

            call.respond(events)
        }

        get("/get-event") {
            val eventQuery = eventQueryOf(call.request.queryParameters).getOrNull()

            if (eventQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val eventOrError = SQLDatabase.getEvent(eventQuery)

            if (eventOrError is Error) {
                call.respond(eventOrError.error.getHttpStatusCode())
                return@get
            }

            call.respond(eventOrError)
        }

        get("/get-match") {
            val matchQuery = matchQueryOf(call.request.queryParameters).getOrNull()

            if (matchQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val matchOrError = SQLDatabase.getMatch(matchQuery)

            if (matchOrError is Error) {
                call.respond(matchOrError.error.getHttpStatusCode())
                return@get
            }

            val match = (matchOrError as Success).value

            call.respond(match)
        }

        get("/get-participant") {
            val participantQuery = participantQueryOf(call.request.queryParameters).getOrNull()

            if (participantQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val participantOrError = SQLDatabase.getParticipant(participantQuery)

            if (participantOrError is Error) {
                call.respond(participantOrError.error.getHttpStatusCode())
                return@get
            }

            val participant = (participantOrError as Success).value

            call.respond(participant)
        }

        post("/new-team") {
            val team = call.receive<Team>()

            val httpStatusCode = when (val result = SQLDatabase.insertTeam(team)) {
                is Success -> HttpStatusCode.Created
                is Error -> result.error.getHttpStatusCode()
            }

            call.respond(httpStatusCode)
        }

        post("/new-season") {
            val season = call.receive<Season>()

            val teamQuery = teamQueryOf(call.request.queryParameters).getOrNull()

            if (teamQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val httpStatusCode = when (val result = SQLDatabase.insertSeason(season, teamQuery)) {
                is Success -> HttpStatusCode.Created
                is Error -> result.error.getHttpStatusCode()
            }

            call.respond(httpStatusCode)
        }

        post("/new-robot") {
            val robot = call.receive<Robot>()

            val seasonQuery = seasonQueryOf(call.request.queryParameters).getOrNull()

            if (seasonQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val httpStatusCode = when (val result = SQLDatabase.insertRobot(robot, seasonQuery)) {
                is Success -> HttpStatusCode.Created
                is Error -> result.error.getHttpStatusCode()
            }

            call.respond(httpStatusCode)
        }

        post("/add-event") {
            val seasonQuery = seasonQueryOf(call.request.queryParameters).getOrNull()
            val eventQuery = eventQueryOf(call.request.queryParameters).getOrNull()

            if (seasonQuery == null || eventQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val httpStatusCode = when (val result = SQLDatabase.insertSeasonEvent(eventQuery, seasonQuery)) {
                is Success -> HttpStatusCode.Created
                is Error -> result.error.getHttpStatusCode()
            }

            call.respond(httpStatusCode)
        }

        post("/new-event") {
            val event = call.receive<Event>()

            val httpStatusCode = when (val result = SQLDatabase.insertEvent(event)) {
                is Success -> HttpStatusCode.Created
                is Error -> result.error.getHttpStatusCode()
            }

            call.respond(httpStatusCode)
        }

        post("/new-match") {
            val match = call.receive<Match>()

            // TODO
            assert(match.alliances.isEmpty())

            val eventQuery = eventQueryOf(call.request.queryParameters).getOrNull()

            if (eventQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val httpStatusCode = when (val result = SQLDatabase.insertMatch(match, eventQuery)) {
                is Success -> HttpStatusCode.Created
                is Error -> result.error.getHttpStatusCode()
            }

            call.respond(httpStatusCode)
        }

        post("/new-alliance") {
            val alliance = call.receive<Alliance>()

            val matchQuery = matchQueryOf(call.request.queryParameters).getOrNull()

            if (matchQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val httpStatusCode = when (val result = SQLDatabase.insertAlliance(alliance, matchQuery)) {
                is Success -> HttpStatusCode.Created
                is Error -> result.error.getHttpStatusCode()
            }

            call.respond(httpStatusCode)
        }

        post("/new-participant") {
            val participant = call.receive<Participant>()

            val allianceQuery = allianceQueryOf(call.request.queryParameters).getOrNull()

            if (allianceQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val httpStatusCode = when (val result = SQLDatabase.insertParticipant(participant, allianceQuery)) {
                is Success -> HttpStatusCode.Created
                is Error -> result.error.getHttpStatusCode()
            }

            call.respond(httpStatusCode)
        }

        post("/add-metrics") {
            val metrics = call.receive<Map<String, String>>()

            val participantQuery = participantQueryOf(call.request.queryParameters).getOrNull()

            if (participantQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            var error: DatabaseError? = null

            val result = SQLDatabase.insertParticipantMetrics(metrics, participantQuery)

            if (result is Error) {
                error = result.error
            }

            if (error != null) {
                call.respond(error.getHttpStatusCode())
                return@post
            }

            call.respond(HttpStatusCode.Created)
        }
    }
}
