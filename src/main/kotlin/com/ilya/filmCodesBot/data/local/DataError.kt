package com.ilya.filmCodesBot.data.local

sealed class DataError : Throwable() {
    data object FilmAlreadyExists : DataError() { private fun readResolve(): Any = FilmAlreadyExists }
    data object FilmNotFound : DataError() { private fun readResolve(): Any = FilmNotFound }
    data object UserAlreadyExists : DataError() { private fun readResolve(): Any = UserAlreadyExists }
    data object UserNotFound : DataError() { private fun readResolve(): Any = UserNotFound }
}

fun DataError.resolve(): String {
    return when (this) {
        DataError.FilmAlreadyExists -> "Фильм с таким номером уже сущетсвует"
        DataError.FilmNotFound -> "Фильм не найден"
        DataError.UserAlreadyExists -> "Ожидайте подтверждения"
        DataError.UserNotFound -> "Не получается найти данные пользователя"
    }
}
