package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.event.Event
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.events() {
    route("/events") {
        get {
            val events = db.getEvents()

            call.respond(events)
        }

        get("/{region}") {
            val region: String = call.parameters["region"]!!

            val events: List<Event> = db.getEventsByRegion(region)

            call.respond(events)
        }

        get("/{region}/{year}") {
            val region: String = call.parameters["region"]!!
            val year: Int = call.parameters["year"]!!.toInt()

            val events = db.getEventsByRegion(region).filter { it.year == year }

            call.respond(events)
        }
    }
}
