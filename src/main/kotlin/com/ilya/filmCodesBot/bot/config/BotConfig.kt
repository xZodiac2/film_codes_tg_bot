package com.ilya.filmCodesBot.bot.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("application.properties")
data class BotConfig(
    @Value("\${bot.token}") val botToken: String,
    @Value("\${bot.name}") val botName: String,
    @Value("\${admin.id}") val adminId: Long
)