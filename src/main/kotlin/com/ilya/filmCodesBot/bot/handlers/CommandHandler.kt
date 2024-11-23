package com.ilya.filmCodesBot.bot.handlers

import com.ilya.filmCodesBot.bot.fsm.FsmContext
import com.ilya.filmCodesBot.bot.fsm.FsmState
import com.ilya.filmCodesBot.bot.models.handleable.Command
import com.ilya.filmCodesBot.bot.models.handleable.KeyboardAction
import com.ilya.filmCodesBot.bot.models.fsmStates.WriteFilmFsmState
import com.ilya.filmCodesBot.core.buildReplyKeyboard
import com.ilya.filmCodesBot.core.sendMessage
import com.ilya.filmCodesBot.data.UsersCountRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton

class CommandHandler(
    private val telegramApiExecutor: DefaultAbsSender,
    private val usersCountRepository: UsersCountRepository,
    private val fsmContext: FsmContext
) : Handler<Command> {

    override fun handle(update: Update, handleable: Command?) {
        when (handleable) {
            Command.START -> onStart(update)
            Command.WRITE -> onWrite(update.message.from.id)
            Command.STOP_WRITE -> onStopWrite(update.message.from.id)
            Command.GET_COUNTERS -> onGetCounters(update)
            null -> Unit
        }
    }

    private fun onGetCounters(update: Update) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val counters = usersCountRepository.getCounters()
            telegramApiExecutor.sendMessage(
                chatId = update.message.from.id,
                text = """
                    Подписалось: ${counters.subscribed}
                    Подтверждено: ${counters.confirmed}
                """.trimIndent()
            )
        }
    }

    private fun onStopWrite(chatId: Long) {
        fsmContext.states[chatId] = FsmState.None

        telegramApiExecutor.sendMessage(
            chatId = chatId,
            text = "Запись закончена"
        )
    }

    private fun onStart(update: Update) {
        telegramApiExecutor.sendMessage(
            chatId = update.message.from.id,
            text = "Доброго времени суток, ${update.message.from.firstName}! Здесь вы сможете узнать названия интересующих вас фильмов по коду",
            markup = buildReplyKeyboard(KeyboardButton(KeyboardAction.INPUT_FILM_CODE.value()))
        )
    }

    private fun onWrite(chatId: Long) {
        if (fsmContext.states[chatId] == WriteFilmFsmState.WRITING) {
            telegramApiExecutor.sendMessage(chatId, "Режим записи уже включён")
            return
        }

        fsmContext.states[chatId] = WriteFilmFsmState.WRITING
        telegramApiExecutor.sendMessage(chatId, "Включён режим записи. Отправьте данные фильма для сохранения")
    }

}