package com.ilya.filmCodesBot.bot.fsm

class FsmContext(var states: MutableMap<Long, FsmState>) {
    companion object {
        fun default(): FsmContext {
            return FsmContext(mutableMapOf())
        }
    }
}