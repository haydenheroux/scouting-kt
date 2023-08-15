package io.github.haydenheroux.scouting.errors

import io.ktor.http.*

sealed class DatabaseError

object DatabaseUnknownError : DatabaseError()
data class DatabaseThingExists(val thing: String) : DatabaseError()
data class DatabaseThingDoesNotExist(val thing: String) : DatabaseError()

fun DatabaseError.getHttpStatusCode(): HttpStatusCode {
    return when (this) {
        is DatabaseUnknownError -> HttpStatusCode.InternalServerError
        is DatabaseThingExists -> HttpStatusCode.Conflict
        is DatabaseThingDoesNotExist -> HttpStatusCode.NotFound
    }
}