package com.ilya.filmCodesBot.data.repository

import com.ilya.filmCodesBot.bot.models.film.Film
import com.ilya.filmCodesBot.data.FilmsRepository
import com.ilya.filmCodesBot.data.local.FilmsLocalRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.telegram.telegrambots.meta.api.objects.PhotoSize

@Repository("films")
class FilmsRepositoryImpl(@Qualifier("filmsLocal") private val filmsLocalRepository: FilmsRepository) : FilmsRepository {

    override suspend fun save(text: String, image: PhotoSize): Result<Unit> {
        return filmsLocalRepository.save(text, image)
    }

    override suspend fun get(code: Int): Result<Film> {
        return filmsLocalRepository.get(code)
    }

}
