package com.ilya.filmCodesBot.bot.handlers

import com.ilya.filmCodesBot.bot.models.handleable.Handleable
import org.telegram.telegrambots.meta.api.objects.Update

interface Handler<T : Handleable> {
    fun handle(update: Update, handleable: T?)
}