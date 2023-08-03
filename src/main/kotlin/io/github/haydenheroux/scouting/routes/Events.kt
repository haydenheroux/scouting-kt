package io.github.haydenheroux.scouting.routes

import io.github.haydenheroux.scouting.database.db
import io.github.haydenheroux.scouting.models.enums.regionOf
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.events() {
    route("/events") {
        get {
            val events = db.getEvents()

            call.respond(FreeMarkerContent("events.ftl", mapOf("events" to events)))
        }

        get("/{region}") {
            val region = regionOf[call.parameters["region"]]!!

            val events = db.getEvents().filter { it.region == region }

            call.respond(events)
        }

        get("/{region}/{year}") {
            val region = regionOf[call.parameters["region"]]!!
            val year = call.parameters["year"]!!.toInt()

            val events = db.getEvents().filter { it.region == region && it.year == year }

            call.respond(events)
        }
    }
}
