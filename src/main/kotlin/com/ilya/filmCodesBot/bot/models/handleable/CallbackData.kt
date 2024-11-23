package com.ilya.filmCodesBot.bot.models.handleable

sealed class CallbackData : Handleable {

    abstract val data: String

    data class ConfirmUser(override val data: String) : CallbackData()
    data object Subscribed : CallbackData() {
        override val data: String
            get() = "subscribed"
    }

    companion object {
        fun valueFrom(from: String): CallbackData? {
            return when {
                from.startsWith("confirm") -> ConfirmUser(from)
                from == "subscribed" -> Subscribed
                else -> null
            }
        }
    }

}