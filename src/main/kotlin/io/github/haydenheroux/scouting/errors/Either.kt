package io.github.haydenheroux.scouting.errors

sealed class Either<out T, out E>

data class Success<out T>(val value: T) : Either<T, Nothing>()
data class Error<out E>(val error: E) : Either<Nothing, E>()