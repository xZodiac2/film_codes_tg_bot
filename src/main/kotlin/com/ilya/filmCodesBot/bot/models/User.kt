package com.ilya.filmCodesBot.bot.models

import org.telegram.telegrambots.meta.api.objects.PhotoSize

data class User(
    val id: Long,
    val name: String,
    val username: String,
    val photo: PhotoSize?,
    val confirmed: Boolean
)