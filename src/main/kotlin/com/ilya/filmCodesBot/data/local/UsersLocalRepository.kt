package com.ilya.filmCodesBot.data.local

import com.ilya.filmCodesBot.bot.models.User
import com.ilya.filmCodesBot.data.UsersRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Repository
import java.io.File
import java.io.IOException

@Repository("usersLocal")
class UsersLocalRepository(moshi: Moshi) : UsersRepository {

    private val file = File(USERS_FILE_PATH)
    private val mutex = Mutex()
    private val adapter = moshi.adapter<List<User>>(Types.newParameterizedType(List::class.java, User::class.java))

    override suspend fun save(user: User): Result<Unit> {
        mutex.withLock {
            file.bufferedReader().use { reader ->
                val usersJson = reader.readText()
                val users = try {
                    adapter.fromJson(usersJson) ?: listOf()
                } catch (e: IOException) {
                    listOf()
                }

                if (user.id in users.map { it.id }) return Result.failure(DataError.UserAlreadyExists)
            }

            var usersJson: String
            file.bufferedReader().use { usersJson = it.readText() }
            file.outputStream().use { output ->
                val users = try {
                    adapter.fromJson(usersJson)?.toMutableList() ?: mutableListOf()
                } catch (e: IOException) {
                    mutableListOf()
                }
                users += user
                val newUsersJson = adapter.toJson(users)
                output.write(newUsersJson.toByteArray())
            }

            return Result.success(Unit)
        }
    }

    override suspend fun get(id: Long): Result<User> {
        mutex.withLock {
            file.bufferedReader().use { reader ->
                val users = try {
                    adapter.fromJson(reader.readText()) ?: listOf()
                } catch (e: IOException) {
                    listOf()
                }

                val user = users.find { it.id == id } ?: return Result.failure(DataError.UserNotFound)
                return Result.success(user)
            }
        }
    }

    override suspend fun getAll(): List<User> {
        mutex.withLock {
            file.bufferedReader().use { reader ->
                val users = try {
                    adapter.fromJson(reader.readText()) ?: listOf()
                } catch (e: IOException) {
                    listOf()
                }
                return users
            }
        }
    }

    override suspend fun changeConfirmed(id: Long, confirmed: Boolean): Result<User> {
        mutex.withLock {
            var usersJson: String
            file.bufferedReader().use { usersJson = it.readText() }
            file.outputStream().use { output ->
                val users = try {
                    adapter.fromJson(usersJson)?.toMutableList() ?: mutableListOf()
                } catch (e: IOException) {
                    mutableListOf()
                }
                val updatableUserIndex = users.indexOfFirst { it.id == id }
                if (updatableUserIndex == -1) return Result.failure(DataError.UserNotFound)

                val updatedUser = users[updatableUserIndex].copy(confirmed = confirmed)
                users[updatableUserIndex] = updatedUser

                val newUsersJson = adapter.toJson(users)
                output.write(newUsersJson.toByteArray())
                return Result.success(updatedUser)
            }
        }
    }

    companion object {
        private const val USERS_FILE_PATH = "./database/users.json"
    }

}