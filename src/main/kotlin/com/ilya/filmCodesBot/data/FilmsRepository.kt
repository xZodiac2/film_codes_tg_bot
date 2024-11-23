package com.ilya.filmCodesBot.data

import com.ilya.filmCodesBot.bot.models.film.Film
import org.telegram.telegrambots.meta.api.objects.PhotoSize

interface FilmsRepository {
    suspend fun save(text: String, image: PhotoSize): Result<Unit>
    suspend fun get(code: Int): Result<Film>
}