package com.ilya.filmCodesBot.bot.models.handleable

enum class KeyboardAction : Handleable {
    INPUT_FILM_CODE,
    BACK;

    fun value(): String {
        return when (this) {
            INPUT_FILM_CODE -> "\uD83D\uDD0D Найти фильм"
            BACK -> "Назад"
        }
    }

    companion object {
        fun valueFrom(from: String): KeyboardAction? {
            return when (from) {
                "\uD83D\uDD0D Найти фильм" -> INPUT_FILM_CODE
                "Назад" -> BACK
                else -> null
            }
        }
    }

}
