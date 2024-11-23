package com.ilya.filmCodesBot.bot.confirmation

import com.ilya.filmCodesBot.bot.models.User
import com.ilya.filmCodesBot.data.UsersRepository
import com.ilya.filmCodesBot.data.local.DataError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PendingUsersResolver(private val usersRepository: UsersRepository) {

    private val scope = CoroutineScope(Dispatchers.IO)

    fun checkUserConfirmed(id: Long, onChecked: (Boolean) -> Unit) {
        scope.launch {
            val result = usersRepository.get(id)
            onChecked(result.fold(
                onSuccess = { it.confirmed },
                onFailure = { false }
            ))
        }
    }

    fun addUser(user: User, onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        scope.launch {
            val result = usersRepository.save(user)
            result.fold(
                onSuccess = { onSuccess() },
                onFailure = { onFailure(it) }
            )
        }
    }

    fun confirmUser(id: Long, onConfirmSuccess: (User) -> Unit, onConfirmFailure: (DataError) -> Unit) {
        scope.launch {
            val result = usersRepository.changeConfirmed(id = id, confirmed = true)
            result.fold(
                onSuccess = { onConfirmSuccess(it) },
                onFailure = { onConfirmFailure(it as DataError) }
            )
        }
    }

}