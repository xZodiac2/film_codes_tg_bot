package com.ilya.filmCodesBot.bot.init

import com.ilya.filmCodesBot.bot.FilmCodesTelegramBot
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Component
class BotInitializer(private val bot: FilmCodesTelegramBot) {

    @EventListener(ContextRefreshedEvent::class)
    fun initBot() {
        val api = TelegramBotsApi(DefaultBotSession::class.java)
        api.registerBot(bot)
    }

}

