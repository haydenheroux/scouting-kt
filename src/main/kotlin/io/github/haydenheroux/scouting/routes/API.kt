package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.event.Event
import io.github.haydenheroux.scouting.models.event.eventQuery
import io.github.haydenheroux.scouting.models.match.Match
import io.github.haydenheroux.scouting.models.match.Participant
import io.github.haydenheroux.scouting.models.match.matchQuery
import io.github.haydenheroux.scouting.models.match.participantQuery
import io.github.haydenheroux.scouting.models.team.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.api() {
    route("/api") {
        get("/get-teams") {
            val teams = db.getTeams().getOrNull()

            if (teams == null) {
                call.respond(HttpStatusCode.InternalServerError)
                return@get
            }

            call.respond(teams.map { it.branch().tree().subtree() })
        }

        get("/get-team") {
            val teamQuery = call.request.queryParameters.teamQuery().getOrNull()

            if (teamQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val team = db.getTeamByQuery(teamQuery).getOrNull()

            if (team == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(team.branch().tree().subtree())
        }

        get("/get-season") {
            val seasonQuery = call.request.queryParameters.seasonQuery().getOrNull()

            if (seasonQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val season = db.getSeasonByQuery(seasonQuery).getOrNull()

            if (season == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(season.branch().tree().subtree())
        }

        get("/get-robot") {
            val robotQuery = call.request.queryParameters.robotQuery().getOrNull()

            if (robotQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val robot = db.getRobotByQuery(robotQuery).getOrNull()

            if (robot == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(robot.branch().tree().subtree())
        }

        get("/get-events") {
            val events = db.getEvents().getOrNull()

            if (events == null) {
                call.respond(HttpStatusCode.InternalServerError)
                return@get
            }

            call.respond(events.map { it.branch().tree().subtree() })
        }

        get("/get-event") {
            val eventQuery = call.request.queryParameters.eventQuery().getOrNull()

            if (eventQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val event = db.getEventByQuery(eventQuery).getOrNull()

            if (event == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(event.branch().tree().subtree())
        }

        get("/get-match") {
            val matchQuery = call.request.queryParameters.matchQuery().getOrNull()

            if (matchQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val match = db.getMatchByQuery(matchQuery).getOrNull()

            if (match == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(match.branch().tree().subtree())
        }

        get("/get-participant") {
            val participantQuery = call.request.queryParameters.participantQuery().getOrNull()

            if (participantQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val participant = db.getParticipantByQuery(participantQuery).getOrNull()

            if (participant == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(participant.branch().tree().subtree())
        }

        post("/new-team") {
            val team = call.receive<Team>()

            db.insertTeam(team).getOrNull()?.let {
                call.respond(HttpStatusCode.OK)
            } ?: run {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post("/new-season") {
            val season = call.receive<Season>()

            val teamQuery = call.request.queryParameters.teamQuery().getOrNull()

            if (teamQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            db.insertSeason(season, teamQuery).getOrNull()?.let {
                call.respond(HttpStatusCode.OK)
            } ?: run {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post("/new-robot") {
            val robot = call.receive<Robot>()

            val seasonQuery = call.request.queryParameters.seasonQuery().getOrNull()

            if (seasonQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            db.insertRobot(robot, seasonQuery).getOrNull()?.let {
                call.respond(HttpStatusCode.OK)
            } ?: run {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post("/add-event") {
            val seasonQuery = call.request.queryParameters.seasonQuery().getOrNull()
            val eventQuery = call.request.queryParameters.eventQuery().getOrNull()

            if (seasonQuery == null || eventQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            db.insertSeasonEvent(eventQuery, seasonQuery).getOrNull()?.let {
                call.respond(HttpStatusCode.OK)
            } ?: run {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post("/new-event") {
            val event = call.receive<Event>()

            db.insertEvent(event).getOrNull()?.let {
                call.respond(HttpStatusCode.OK)
            } ?: run {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post("/new-match") {
            val match = call.receive<Match>()

            assert(match.participants.isEmpty())

            val eventQuery = call.request.queryParameters.eventQuery().getOrNull()

            if (eventQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            db.insertMatch(match, eventQuery).getOrNull()?.let {
                call.respond(HttpStatusCode.OK)
            } ?: run {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post("/new-participant") {
            val participant = call.receive<Participant>()

            val teamQuery = call.request.queryParameters.teamQuery().getOrNull()
            val matchQuery = call.request.queryParameters.matchQuery().getOrNull()

            if (teamQuery == null || matchQuery == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            db.insertParticipant(participant, teamQuery, matchQuery).getOrNull()?.let {
                call.respond(HttpStatusCode.OK)
            } ?: run {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}
