package com.ilya.filmCodesBot.data.local

import com.ilya.filmCodesBot.bot.models.film.Film
import com.ilya.filmCodesBot.data.FilmsRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Repository
import org.telegram.telegrambots.meta.api.objects.PhotoSize
import java.io.File
import java.io.IOException

@Repository("filmsLocal")
class FilmsLocalRepository(moshi: Moshi) : FilmsRepository {

    private val adapter = moshi.adapter<List<Film>>(Types.newParameterizedType(List::class.java, Film::class.java))
    private val file = File(FILMS_DATA_PATH)
    private val mutex = Mutex()

    override suspend fun save(text: String, image: PhotoSize): Result<Unit> {
        mutex.withLock {
            val film = parseFilm(text, image.fileId)

            file.bufferedReader().use { reader ->
                val filmsJson = reader.readText()
                val films = try {
                    adapter.fromJson(filmsJson) ?: listOf()
                } catch (e: IOException) {
                    listOf()
                }

                if (film.code in films.map { it.code }) return Result.failure(DataError.FilmAlreadyExists)
            }

            var filmsJson: String
            file.bufferedReader().use { filmsJson = it.readText() }
            file.outputStream().use { output ->
                val films = try {
                    adapter.fromJson(filmsJson)?.toMutableList() ?: mutableListOf()
                } catch (e: IOException) {
                    mutableListOf()
                }
                films += film
                val newFilmsJson = adapter.toJson(films)
                output.write(newFilmsJson.toByteArray())
            }

            return Result.success(Unit)
        }
    }

    private fun parseFilm(data: String, imageId: String): Film {
        val film = Film("", 0, "", "", 0, "", "", "")
        val filmWithText = insertText(data, film)
        return filmWithText.copy(imageFileId = imageId)
    }

    private fun insertText(text: String, initial: Film): Film {
        val lines = text.lines()

        val titleLine = lines[0]
        val type = titleLine.substringBefore("№").trim().removePrefix("⭐\uFE0F")
        val code = titleLine.substringBefore("-").trim().substringAfter("№").toInt()
        val name = titleLine.substringAfter(" - ").trim()

        val indexYear = lines.indexOfFirst { it.startsWith("\uD83D\uDD39Год: ") }
        val year = lines[indexYear].substringAfter(":").trim().toInt()

        val indexGenre = lines.indexOfFirst { it.startsWith("\uD83D\uDD39Жанр: ") }
        val genre = lines[indexGenre].substringAfter(":").trim()

        val indexCountry = lines.indexOfFirst { it.startsWith("\uD83D\uDD39Страна: ") }
        val country = lines[indexCountry].substringAfter(":").trim()

        val descriptionIndex = lines.indexOfFirst { it.startsWith("\uD83D\uDD38Описание:") }
        val description = lines[descriptionIndex + 1].trim()

        return initial.copy(
            type = type,
            code = code,
            name = name,
            year = year,
            genre = genre,
            country = country,
            description = description
        )
    }

    override suspend fun get(code: Int): Result<Film> {
        mutex.withLock {
            file.bufferedReader().use { reader ->
                val json = reader.readText()

                val films = try {
                    adapter.fromJson(json)
                } catch (e: IOException) {
                    listOf()
                }

                val film = films?.find { it.code == code } ?: return Result.failure(DataError.FilmNotFound)
                return Result.success(film)
            }
        }
    }

    companion object {
        private const val FILMS_DATA_PATH = "./database/films.json"
    }

}