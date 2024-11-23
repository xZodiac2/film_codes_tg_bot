package com.ilya.filmCodesBot.bot.handlers

import com.ilya.filmCodesBot.bot.fsm.FsmContext
import com.ilya.filmCodesBot.bot.fsm.FsmState
import com.ilya.filmCodesBot.bot.models.film.Film
import com.ilya.filmCodesBot.bot.models.fsmStates.GetFilmFsmState
import com.ilya.filmCodesBot.bot.models.fsmStates.WriteFilmFsmState
import com.ilya.filmCodesBot.core.sendMessage
import com.ilya.filmCodesBot.core.sendPhoto
import com.ilya.filmCodesBot.data.FilmsRepository
import com.ilya.filmCodesBot.data.local.DataError
import com.ilya.filmCodesBot.data.local.resolve
import kotlinx.coroutines.*
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.objects.PhotoSize
import org.telegram.telegrambots.meta.api.objects.Update

class FsmStateHandler(
    private val telegramApiExecutor: DefaultAbsSender,
    private val fsmContext: FsmContext,
    private val repository: FilmsRepository,
) : Handler<FsmState> {

    private val scope = CoroutineScope(SupervisorJob())

    override fun handle(update: Update, handleable: FsmState?) {
        when (fsmContext.states[update.message.from.id]) {
            WriteFilmFsmState.WRITING -> onWriteFilmData(
                update.message.chatId,
                update.message.caption,
                update.message.photo.maxBy { it.fileSize }
            )

            GetFilmFsmState.GET_FILM_BY_CODE -> onCodeInput(update, update.message.text)
            FsmState.None -> Unit
        }
    }

    private fun onCodeInput(update: Update, text: String) {
        val getFilmExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            when (throwable) {
                is NumberFormatException -> {
                    telegramApiExecutor.sendMessage(
                        chatId = update.message.chatId,
                        text = "Некорректный код: $text"
                    )
                }

                else -> {
                    telegramApiExecutor.sendMessage(
                        chatId = update.message.chatId,
                        text = "Что-то пошло не так"
                    )
                }
            }
        }

        scope.launch(Dispatchers.IO + getFilmExceptionHandler) {
            val code = text.toInt()
            val result = repository.get(code)

            result.onSuccess { sendFilm(update, it) }
            result.onFailure { sendErrorMessage(update.message.chatId, it as DataError) }
        }
    }

    private fun sendFilm(update: Update, film: Film) {
        telegramApiExecutor.sendPhoto(
            chatId = update.message.chatId,
            photoId = film.imageFileId,
            text = """
                ⭐️${film.type}  №${film.code} - ${film.name}

                🔹Год: ${film.year}
                🔹Жанр: ${film.genre}
                🔹Страна: ${film.country}

                🔸Описание:
                ${film.description}
            """.trimIndent()
        )
    }

    private fun onWriteFilmData(chatId: Long, text: String, image: PhotoSize) {
        scope.launch(Dispatchers.IO) {
            val result = repository.save(text, image)
            result.fold(
                onSuccess = {
                    telegramApiExecutor.sendMessage(
                        chatId = chatId,
                        text = "Успешно сохранено"
                    )
                },
                onFailure = { sendErrorMessage(chatId, it as DataError) }
            )
        }
    }

    private fun sendErrorMessage(chatId: Long, error: DataError) {
        telegramApiExecutor.sendMessage(
            chatId = chatId,
            text = error.resolve()
        )
    }

}
