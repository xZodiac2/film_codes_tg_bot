package com.ilya.filmCodesBot.data

import com.ilya.filmCodesBot.bot.models.User

interface UsersRepository {
    suspend fun save(user: User): Result<Unit>
    suspend fun get(id: Long): Result<User>
    suspend fun getAll(): List<User>
    suspend fun changeConfirmed(id: Long, confirmed: Boolean): Result<User>
}