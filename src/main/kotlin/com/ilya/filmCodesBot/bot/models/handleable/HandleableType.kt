package com.ilya.filmCodesBot.bot.models.handleable

interface Handleable

enum class HandleableType {
    COMMAND,
    CALLBACK_DATA,
    KEYBOARD_ACTION,
    FSM_STATE,
}