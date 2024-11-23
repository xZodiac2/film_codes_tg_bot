package com.ilya.filmCodesBot.bot

import com.ilya.filmCodesBot.bot.config.BotConfig
import com.ilya.filmCodesBot.bot.confirmation.PendingUsersResolver
import com.ilya.filmCodesBot.bot.fsm.FsmContext
import com.ilya.filmCodesBot.bot.fsm.FsmState
import com.ilya.filmCodesBot.bot.handlers.CallbackDataHandler
import com.ilya.filmCodesBot.bot.handlers.CommandHandler
import com.ilya.filmCodesBot.bot.handlers.FsmStateHandler
import com.ilya.filmCodesBot.bot.handlers.KeyboardActionHandler
import com.ilya.filmCodesBot.bot.models.handleable.CallbackData
import com.ilya.filmCodesBot.bot.models.handleable.Command
import com.ilya.filmCodesBot.bot.models.handleable.HandleableType
import com.ilya.filmCodesBot.bot.models.handleable.KeyboardAction
import com.ilya.filmCodesBot.core.sendMessage
import com.ilya.filmCodesBot.data.FilmsRepository
import com.ilya.filmCodesBot.data.UsersCountRepository
import com.ilya.filmCodesBot.data.UsersRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook
import org.telegram.telegrambots.meta.api.objects.Update

@Component
final class FilmCodesTelegramBot(
    private val config: BotConfig,
    @Qualifier("films") filmsRepository: FilmsRepository,
    @Qualifier("users") usersRepository: UsersRepository,
    @Qualifier("usersCount") usersCountRepository: UsersCountRepository
) : TelegramLongPollingBot(config.botToken) {

    private val fsmContext = FsmContext.default()
    private val usersResolver = PendingUsersResolver(usersRepository)

    private val callbackDataHandler = CallbackDataHandler(this, usersResolver, usersCountRepository, config.adminId)
    private val commandHandler = CommandHandler(this, usersCountRepository, fsmContext)
    private val fsmStateHandler = FsmStateHandler(this, fsmContext, filmsRepository)
    private val keyboardActionHandler = KeyboardActionHandler(this, usersResolver, fsmContext)

    override fun onUpdateReceived(update: Update) {
        val deleteWebhook = DeleteWebhook().apply {
            this.dropPendingUpdates = true
        }
        execute(deleteWebhook)
        sendMessage(
            chatId = config.adminId,
            text = update.message.from.id.toString()
        )
        val handleable = getHandleableType(update)

        when (handleable) {
            HandleableType.COMMAND -> {
                commandHandler.handle(update, Command.valueFrom(update.message.text))
            }

            HandleableType.CALLBACK_DATA -> {
                callbackDataHandler.handle(update, CallbackData.valueFrom(update.callbackQuery.data))
            }

            HandleableType.KEYBOARD_ACTION -> {
                keyboardActionHandler.handle(update, KeyboardAction.valueFrom(update.message.text))
            }

            HandleableType.FSM_STATE -> {
                fsmStateHandler.handle(update, null)
            }

            null -> Unit
        }

    }

    private fun getHandleableType(update: Update): HandleableType? {
        if (update.callbackQuery != null) return HandleableType.CALLBACK_DATA
        update.message ?: return null
        if (update.message?.text?.startsWith("/") == true && update.message?.text?.contains(" ") != true) {
            return HandleableType.COMMAND
        }
        val text = update.message?.text ?: update.message?.caption ?: return null
        if (text in KeyboardAction.entries.map { it.value() }) return HandleableType.KEYBOARD_ACTION
        val state = fsmContext.states[update.message.from.id]
        if (state != FsmState.None && state != null) return HandleableType.FSM_STATE

        return null
    }

    override fun getBotUsername(): String {
        return config.botName
    }

}
