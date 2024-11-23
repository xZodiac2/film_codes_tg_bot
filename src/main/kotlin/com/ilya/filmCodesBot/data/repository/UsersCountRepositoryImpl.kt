package com.ilya.filmCodesBot.data.repository

import com.ilya.filmCodesBot.bot.models.UsersCount
import com.ilya.filmCodesBot.data.UsersCountRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository("usersCount")
class UsersCountRepositoryImpl(
    @Qualifier("usersCountLocal")
    private val localRepository: UsersCountRepository
) : UsersCountRepository {

    override suspend fun incrementConfirmed(): UsersCount {
        return localRepository.incrementConfirmed()
    }

    override suspend fun incrementSubscribed(): UsersCount {
        return localRepository.incrementSubscribed()
    }

    override suspend fun getCounters(): UsersCount {
        return localRepository.getCounters()
    }

}