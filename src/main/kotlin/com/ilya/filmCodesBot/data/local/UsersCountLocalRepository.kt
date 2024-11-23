package com.ilya.filmCodesBot.data.local

import com.ilya.filmCodesBot.bot.models.UsersCount
import com.ilya.filmCodesBot.data.UsersCountRepository
import com.ilya.filmCodesBot.data.repository.UsersCountRepositoryImpl
import com.squareup.moshi.Moshi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Repository
import java.io.File
import java.io.IOException

@Repository("usersCountLocal")
class UsersCountLocalRepository(moshi: Moshi) : UsersCountRepository {

    private val adapter = moshi.adapter(UsersCount::class.java)

    private val file = File(USERS_COUNT_FILENAME)
    private val mutex = Mutex()

    override suspend fun incrementConfirmed(): UsersCount {
        mutex.withLock {
            var json: String
            file.bufferedReader().use { json = it.readText() }
            file.outputStream().use { output ->
                val usersCount = try {
                    adapter.fromJson(json)
                } catch (e: IOException) {
                    UsersCount(0, 0)
                } ?: UsersCount(0, 0)
                val incremented = usersCount.copy(confirmed = usersCount.confirmed + 1)
                output.write(adapter.toJson(incremented).toByteArray())
                return incremented
            }
        }
    }

    override suspend fun incrementSubscribed(): UsersCount {
        mutex.withLock {
            var json: String
            file.bufferedReader().use { json = it.readText() }
            file.outputStream().use { output ->
                val usersCount = try {
                    adapter.fromJson(json)
                } catch (e: IOException) {
                    UsersCount(0, 0)
                } ?: UsersCount(0, 0)
                val incremented = usersCount.copy(subscribed = usersCount.subscribed + 1)
                output.write(adapter.toJson(incremented).toByteArray())
                return incremented
            }
        }
    }

    override suspend fun getCounters(): UsersCount {
        mutex.withLock {
            val json = file.readText()
            val count = try {
                adapter.fromJson(json) ?: UsersCount(-10, -10)
            } catch (e: IOException) {
                UsersCount(-10, -10)
            }
            return count
        }
    }

    companion object {
        private const val USERS_COUNT_FILENAME = "./database/users_count.json"
    }

}