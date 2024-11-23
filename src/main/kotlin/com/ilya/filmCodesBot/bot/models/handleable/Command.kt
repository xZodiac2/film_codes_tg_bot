package com.ilya.filmCodesBot.bot.models.handleable

enum class Command : Handleable {
    START,
    WRITE,
    STOP_WRITE,
    GET_COUNTERS;

    companion object {
        fun valueFrom(from: String): Command? {
            return when (from) {
                "/start" -> START
                "/24rfgt57ujki_write" -> WRITE
                "/24rfgt57ujki_stop_write" -> STOP_WRITE
                "/24rfgt57ujki_get_counters" -> GET_COUNTERS
                else -> null
            }
        }
    }

}