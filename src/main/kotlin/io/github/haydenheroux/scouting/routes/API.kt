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
            val teams = db.getTeams()

            call.respond(teams.map { it.subtree().tree().subtree() })
        }

        get("/get-team") {
            val teamQuery = call.request.queryParameters.teamQuery().getOrNull()

            teamQuery?.let {
                val team = db.getTeamByQuery(teamQuery)

                call.respond(team.subtree().tree().subtree())
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/get-season") {
            val seasonQuery = call.request.queryParameters.seasonQuery().getOrNull()

            seasonQuery?.let {
                val season = db.getSeasonByQuery(seasonQuery)

                call.respond(season.subtree().tree().subtree())
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/get-robot") {
            val robotQuery = call.request.queryParameters.robotQuery().getOrNull()

            robotQuery?.let {
                val robot = db.getRobotByQuery(robotQuery)

                call.respond(robot.subtree().tree().subtree())
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/get-events") {
            val events = db.getEvents()

            call.respond(events.map { it.subtree().tree().subtree() })
        }

        get("/get-event") {
            val eventQuery = call.request.queryParameters.eventQuery().getOrNull()

            eventQuery?.let {
                val event = db.getEventByQuery(eventQuery)

                call.respond(event.subtree().tree().subtree())
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/get-match") {
            val matchQuery = call.request.queryParameters.matchQuery().getOrNull()

            matchQuery?.let {
                val match = db.getMatchByQuery(matchQuery)

                call.respond(match.subtree().tree().subtree())
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/get-participant") {
            val participantQuery = call.request.queryParameters.participantQuery().getOrNull()

            participantQuery?.let {
                val participant = db.getParticipantByQuery(participantQuery)

                call.respond(participant.subtree().tree().subtree())
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post("/new-team") {
            val team = call.receive<Team>()

            db.insertTeam(team)

            call.respond(HttpStatusCode.OK)
        }

        post("/new-season") {
            val season = call.receive<Season>()

            val teamQuery = call.request.queryParameters.teamQuery().getOrNull()

            teamQuery?.let {
                db.insertSeason(season, teamQuery)

                call.respond(HttpStatusCode.OK)
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post("/new-robot") {
            val robot = call.receive<Robot>()

            val seasonQuery = call.request.queryParameters.seasonQuery().getOrNull()

            seasonQuery?.let {
                db.insertRobot(robot, seasonQuery)

                call.respond(HttpStatusCode.OK)
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post("/add-event") {
            val eventQuery = call.request.queryParameters.eventQuery().getOrNull()

            eventQuery?.let {
                val seasonQuery = call.request.queryParameters.seasonQuery().getOrNull()

                // TODO Find a way to remove these nested null checks
                seasonQuery?.let {
                    db.insertSeasonEvent(eventQuery, seasonQuery)

                    call.respond(HttpStatusCode.OK)
                } ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                }
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post("/new-event") {
            val event = call.receive<Event>()

            db.insertEvent(event)

            call.respond(HttpStatusCode.OK)
        }

        post("/new-match") {
            val match = call.receive<Match>()

            assert(match.participants.isEmpty())

            val eventQuery = call.request.queryParameters.eventQuery().getOrNull()

            eventQuery?.let {
                db.insertMatch(match, eventQuery)

                call.respond(HttpStatusCode.OK)
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post("/new-participant") {
            val participant = call.receive<Participant>()

            val matchQuery = call.request.queryParameters.matchQuery().getOrNull()

            matchQuery?.let {
                val robotQuery = call.request.queryParameters.robotQuery().getOrNull()

                robotQuery?.let {
                    db.insertParticipant(participant, matchQuery, robotQuery)

                    call.respond(HttpStatusCode.OK)
                } ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                }
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}
