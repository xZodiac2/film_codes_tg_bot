package com.ilya.filmCodesBot.data

import com.ilya.filmCodesBot.bot.models.UsersCount

interface UsersCountRepository {
    suspend fun incrementConfirmed(): UsersCount
    suspend fun incrementSubscribed(): UsersCount
    suspend fun getCounters(): UsersCount
}