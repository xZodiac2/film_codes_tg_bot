package com.ilya.filmCodesBot.core

import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.File
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.bots.AbsSender

fun AbsSender.sendMessage(chatId: Long, text: String, markup: ReplyKeyboard? = null, parseMode: String? = null) {
    val sendMessage = SendMessage().apply {
        this.chatId = chatId.toString()
        this.text = text
        this.replyMarkup = markup
        this.parseMode = parseMode
    }
    SendPhoto().photo
    execute(sendMessage)
}

fun DefaultAbsSender.sendPhoto(
    chatId: Long,
    photoId: String,
    text: String,
    markup: ReplyKeyboard? = null,
    parseMode: String? = null
) {
    val getFile = GetFile().apply { fileId = photoId }
    val fileData = execute(getFile)
    val file = with(fileData) { downloadFile(createFile(fileId, fileUniqueId, fileSize, filePath)) }

    val sendPhoto = SendPhoto().apply {
        this.chatId = chatId.toString()
        this.photo = InputFile(file)
        this.caption = text
        this.parseMode = parseMode
        this.replyMarkup = markup
    }
    execute(sendPhoto)
}

fun AbsSender.editMessageText(
    chatId: Long,
    messageId: Int,
    text: String,
    markup: InlineKeyboardMarkup? = null,
    parseMode: String? = null,
) {
    val editText = EditMessageText().apply {
        this.chatId = chatId.toString()
        this.messageId = messageId
        this.text = text
        this.replyMarkup = markup
        this.parseMode = parseMode
    }
    execute(editText)
}


fun AbsSender.editMessageCaption(
    chatId: Long,
    messageId: Int,
    caption: String,
    markup: InlineKeyboardMarkup? = null,
    parseMode: String? = null
) {
    val editCaption = EditMessageCaption().apply {
        this.chatId = chatId.toString()
        this.messageId = messageId
        this.caption = caption
        this.replyMarkup = markup
        this.parseMode = parseMode
    }
    execute(editCaption)
}

fun createFile(id: String, uniqueId: String, size: Long, path: String): File {
    return File(id, uniqueId, size, path)
}
