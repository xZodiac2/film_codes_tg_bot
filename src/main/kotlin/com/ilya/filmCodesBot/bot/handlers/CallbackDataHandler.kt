package com.ilya.filmCodesBot.bot.handlers

import com.ilya.filmCodesBot.bot.confirmation.PendingUsersResolver
import com.ilya.filmCodesBot.bot.models.User
import com.ilya.filmCodesBot.bot.models.handleable.CallbackData
import com.ilya.filmCodesBot.core.*
import com.ilya.filmCodesBot.data.UsersCountRepository
import com.ilya.filmCodesBot.data.local.DataError
import com.ilya.filmCodesBot.data.local.resolve
import kotlinx.coroutines.*
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.methods.GetUserProfilePhotos
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

class CallbackDataHandler(
    private val telegramApiExecutor: DefaultAbsSender,
    private val usersResolver: PendingUsersResolver,
    private val usersCountRepository: UsersCountRepository,
    private val adminId: Long
) : Handler<CallbackData> {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun handle(update: Update, handleable: CallbackData?) {
        when (handleable) {
            is CallbackData.ConfirmUser -> onConfirmUser(update)
            CallbackData.Subscribed -> onSubscribed(update)
            null -> Unit
        }
    }

    private fun onSubscribed(update: Update) {
        telegramApiExecutor.editMessageText(
            chatId = update.callbackQuery.from.id,
            messageId = (update.callbackQuery.message as Message).messageId,
            text = "Ожидайте окончания проверки. Я уведомлю вас, когда можно будет пользоваться ботом"
        )

        val user = with(update.callbackQuery.from) {
            val getProfilePhotos = GetUserProfilePhotos().apply {
                this.userId = id
                this.offset = 0
                this.limit = 1
            }
            val profilePhotoSizes = telegramApiExecutor.execute(getProfilePhotos)
            val profilePhoto = profilePhotoSizes.photos.getOrNull(0)?.maxBy { it.fileSize }
            User(
                id = id,
                name = "$firstName ${lastName ?: ""}",
                username = userName ?: "Имя пользователя не указано",
                photo = profilePhoto,
                confirmed = false
            )
        }
        usersResolver.addUser(
            user = user,
            onSuccess = {
                if (user.photo == null) {
                    telegramApiExecutor.sendMessage(
                        chatId = adminId,
                        text = "${user.name}(@${user.username}) подал заявку на вступление. Если это так, то нажмите \"<b>Подтвердить</b>\". Если в течение нескольких минут заявка на вступление не пришла, то просто проигнорируйте это сообщение",
                        markup = buildInlineKeyboard(
                            buildInlineButton(
                                text = "Подтвердить",
                                callbackData = CallbackData.ConfirmUser("confirm${user.id}")
                            )
                        ),
                        parseMode = ParseMode.HTML
                    )
                } else {
                    telegramApiExecutor.sendPhoto(
                        chatId = adminId,
                        photoId = user.photo.fileId,
                        text = "${user.name}(@${user.username}) подал заявку на вступление. Если это так, то нажмите \"<b>Подтвердить</b>\". Если в течение нескольких минут заявка на вступление не пришла, то просто проигнорируйте это сообщение",
                        markup = buildInlineKeyboard(
                            buildInlineButton(
                                text = "Подтвердить",
                                callbackData = CallbackData.ConfirmUser("confirm${user.id}")
                            )
                        ),
                        parseMode = ParseMode.HTML
                    )
                }
                scope.launch { usersCountRepository.incrementSubscribed() }
            },
            onFailure = {
                telegramApiExecutor.sendMessage(
                    chatId = update.callbackQuery.from.id,
                    text = "Что-то пошло не так: " + (it as DataError).resolve()
                )
                it.printStackTrace()
            }
        )
    }

    private fun onConfirmUser(update: Update) {
        val id = update.callbackQuery.data.substringAfter("confirm").toLong()
        usersResolver.confirmUser(
            id = id,
            onConfirmFailure = { error ->
                telegramApiExecutor.editMessageCaption(
                    chatId = update.callbackQuery.from.id,
                    messageId = (update.callbackQuery.message as Message).messageId,
                    caption = "Ошибка подтверждения: ${error.resolve()}",
                    markup = buildInlineKeyboard(
                        buildInlineButton(
                            text = "Подтвердить",
                            callbackData = CallbackData.ConfirmUser("confirm$id")
                        )
                    )
                )
            },
            onConfirmSuccess = { user ->
                if (user.photo == null) {
                    telegramApiExecutor.editMessageText(
                        chatId = update.callbackQuery.from.id,
                        messageId = (update.callbackQuery.message as Message).messageId,
                        text = "Подтверждено",
                    )
                } else {
                    telegramApiExecutor.editMessageCaption(
                        chatId = update.callbackQuery.from.id,
                        messageId = (update.callbackQuery.message as Message).messageId,
                        caption = "Подтверждено"
                    )
                }
                scope.launch { usersCountRepository.incrementConfirmed() }
                telegramApiExecutor.sendMessage(
                    chatId = user.id,
                    text = "Подписка проверена. Можете пользоваться ботом!"
                )
            }
        )
    }

}