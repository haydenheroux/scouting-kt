package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.sql.SQLDatabase
import io.github.haydenheroux.scouting.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.api() {
    route("/api") {
        get("/get-teams") {
            val teams = SQLDatabase.getTeams().getOrNull()

            if (teams == null) {
                call.respond(HttpStatusCode.InternalServerError)
                return@get
            }

            call.respond(teams)
        }

        get("/get-team") {
            val teamQuery = teamQueryOf(call.request.queryParameters).getOrNull()

            if (teamQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val team = SQLDatabase.getTeam(teamQuery).getOrNull()

            if (team == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(team)
        }

        get("/get-season") {
            val seasonQuery = seasonQueryOf(call.request.queryParameters).getOrNull()

            if (seasonQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val season = SQLDatabase.getSeason(seasonQuery).getOrNull()

            if (season == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(season)
        }

        get("/get-robot") {
            val robotQuery = robotQueryOf(call.request.queryParameters).getOrNull()

            if (robotQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val robot = SQLDatabase.getRobot(robotQuery).getOrNull()

            if (robot == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(robot)
        }

        get("/get-events") {
            val events = SQLDatabase.getEvents().getOrNull()

            if (events == null) {
                call.respond(HttpStatusCode.InternalServerError)
                return@get
            }

            call.respond(events)
        }

        get("/get-event") {
            val eventQuery = eventQueryOf(call.request.queryParameters).getOrNull()

            if (eventQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val event = SQLDatabase.getEvent(eventQuery).getOrNull()

            if (event == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(event)
        }

        get("/get-match") {
            val matchQuery = matchQueryOf(call.request.queryParameters).getOrNull()

            if (matchQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val match = SQLDatabase.getMatch(matchQuery).getOrNull()

            if (match == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(match)
        }

        get("/get-participant") {
            val participantQuery = participantQueryOf(call.request.queryParameters).getOrNull()

            if (participantQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val participant = SQLDatabase.getParticipant(participantQuery).getOrNull()

            if (participant == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(participant)
        }

        get("/get-metric") {
            val metricQuery = metricQueryOf(call.request.queryParameters).getOrNull()

            if (metricQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val metric = SQLDatabase.getMetric(metricQuery).getOrNull()

            if (metric == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(metric)
        }

        post("/new-team") {
            val team = call.receive<Team>()

            SQLDatabase.insertTeam(team).getOrNull()?.let {
                call.respond(HttpStatusCode.Created)
            } ?: run {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post("/new-season") {
            val season = call.receive<Season>()

            val teamQuery = teamQueryOf(call.request.queryParameters).getOrNull()

            if (teamQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            SQLDatabase.insertSeason(season, teamQuery).getOrNull()?.let {
                call.respond(HttpStatusCode.Created)
            } ?: run {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post("/new-robot") {
            val robot = call.receive<Robot>()

            val seasonQuery = seasonQueryOf(call.request.queryParameters).getOrNull()

            if (seasonQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            SQLDatabase.insertRobot(robot, seasonQuery).getOrNull()?.let {
                call.respond(HttpStatusCode.Created)
            } ?: run {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post("/add-event") {
            val seasonQuery = seasonQueryOf(call.request.queryParameters).getOrNull()
            val eventQuery = eventQueryOf(call.request.queryParameters).getOrNull()

            if (seasonQuery == null || eventQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            SQLDatabase.insertSeasonEvent(eventQuery, seasonQuery).getOrNull()?.let {
                call.respond(HttpStatusCode.Created)
            } ?: run {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post("/new-event") {
            val event = call.receive<Event>()

            SQLDatabase.insertEvent(event).getOrNull()?.let {
                call.respond(HttpStatusCode.Created)
            } ?: run {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post("/new-match") {
            val match = call.receive<Match>()

            assert(match.participants.isEmpty())

            val eventQuery = eventQueryOf(call.request.queryParameters).getOrNull()

            if (eventQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            SQLDatabase.insertMatch(match, eventQuery).getOrNull()?.let {
                call.respond(HttpStatusCode.Created)
            } ?: run {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post("/new-participant") {
            val participant = call.receive<Participant>()

            val matchQuery = matchQueryOf(call.request.queryParameters).getOrNull()

            if (matchQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            SQLDatabase.insertParticipant(participant, matchQuery).getOrNull()?.let {
                call.respond(HttpStatusCode.Created)
            } ?: run {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}
