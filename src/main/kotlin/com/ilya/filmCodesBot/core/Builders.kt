package com.ilya.filmCodesBot.core

import com.ilya.filmCodesBot.bot.models.handleable.CallbackData
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

fun buildReplyKeyboard(vararg buttons: KeyboardButton): ReplyKeyboardMarkup {
    return ReplyKeyboardMarkup(buttons.map { KeyboardRow(listOf(it)) }, true, false, false, "Написать сообщение...", false)
}

fun buildInlineKeyboard(vararg buttons: InlineKeyboardButton): InlineKeyboardMarkup {
    return InlineKeyboardMarkup(buttons.map { listOf(it) })
}

fun buildInlineButton(text: String, callbackData: CallbackData): InlineKeyboardButton {
    return InlineKeyboardButton(text, null, callbackData.data, null, null, null, null, null, null)
}


