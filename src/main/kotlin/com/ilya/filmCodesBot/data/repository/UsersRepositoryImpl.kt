package com.ilya.filmCodesBot.data.repository

import com.ilya.filmCodesBot.bot.models.User
import com.ilya.filmCodesBot.data.UsersRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository("users")
class UsersRepositoryImpl(@Qualifier("usersLocal") private val localRepository: UsersRepository) : UsersRepository {

    override suspend fun save(user: User): Result<Unit> {
        return localRepository.save(user)
    }

    override suspend fun get(id: Long): Result<User> {
        return localRepository.get(id)
    }

    override suspend fun getAll(): List<User> {
        return localRepository.getAll()
    }

    override suspend fun changeConfirmed(id: Long, confirmed: Boolean): Result<User> {
        return localRepository.changeConfirmed(id, confirmed)
    }

}