package com.ilya.filmCodesBot.bot.fsm

import com.ilya.filmCodesBot.bot.models.handleable.Handleable

interface FsmState : Handleable {
    object None : FsmState
}