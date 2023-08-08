package io.github.haydenheroux.scouting.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureIgnoreTrailingSlash() {
    install(IgnoreTrailingSlash)
}
