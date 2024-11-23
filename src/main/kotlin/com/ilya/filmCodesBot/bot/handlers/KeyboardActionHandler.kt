package com.ilya.filmCodesBot.bot.handlers

import com.ilya.filmCodesBot.bot.confirmation.PendingUsersResolver
import com.ilya.filmCodesBot.bot.fsm.FsmContext
import com.ilya.filmCodesBot.bot.fsm.FsmState
import com.ilya.filmCodesBot.bot.models.fsmStates.GetFilmFsmState
import com.ilya.filmCodesBot.bot.models.handleable.CallbackData
import com.ilya.filmCodesBot.bot.models.handleable.KeyboardAction
import com.ilya.filmCodesBot.core.buildInlineButton
import com.ilya.filmCodesBot.core.buildInlineKeyboard
import com.ilya.filmCodesBot.core.buildReplyKeyboard
import com.ilya.filmCodesBot.core.sendMessage
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class KeyboardActionHandler(
    private val telegramApiExecutor: DefaultAbsSender,
    private val usersResolver: PendingUsersResolver,
    private val fsmContext: FsmContext
) : Handler<KeyboardAction> {

    override fun handle(update: Update, handleable: KeyboardAction?) {
        when (handleable) {
            KeyboardAction.INPUT_FILM_CODE -> onInputFIlmCode(update)
            KeyboardAction.BACK -> onBack(update.message.from.id)
            null -> Unit
        }
    }

    private fun onBack(chatId: Long) {
        fsmContext.states[chatId] = FsmState.None
        telegramApiExecutor.sendMessage(
            chatId = chatId,
            text = "Ввод кода отключён",
            markup = buildReplyKeyboard(KeyboardButton(KeyboardAction.INPUT_FILM_CODE.value()))
        )
    }

    private fun onInputFIlmCode(update: Update) {
        usersResolver.checkUserConfirmed(
            id = update.message.from.id,
            onChecked = { confirmed ->
                if (confirmed) {
                    fsmContext.states[update.message.from.id] = GetFilmFsmState.GET_FILM_BY_CODE
                    telegramApiExecutor.sendMessage(
                        chatId = update.message.from.id,
                        text = "Введите код",
                        markup = buildReplyKeyboard(KeyboardButton(KeyboardAction.BACK.value()))
                    )
                } else {
                    telegramApiExecutor.sendMessage(
                        chatId = update.message.from.id,
                        text = "Сначала нужно подписаться на канал!\n{ссылка на вступление в тгк}",
                        markup = buildInlineKeyboard(
                            buildInlineButton(
                                text = "Я подписался",
                                callbackData = CallbackData.Subscribed
                            )
                        )
                    )
                }
            }
        )

    }

}