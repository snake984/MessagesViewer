package com.messagesviewer.domain.usecases

import com.messagesviewer.domain.repositories.MessageRepository
import com.messagesviewer.domain.repositories.MessageRepositoryImpl
import com.messagesviewer.domain.repositories.UserRepository
import com.messagesviewer.domain.repositories.UserRepositoryImpl
import java.io.InputStream

class ImportDataUseCase {
    private val userRepository: UserRepository = UserRepositoryImpl()
    private val messageRepository: MessageRepository = MessageRepositoryImpl()

    suspend fun import(source: InputStream): Result =
        try {
            userRepository.importUsers(source)
            messageRepository.importMessages(source)
            Result.Success
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }

    sealed class Result {
        object Success : Result()
        data class Error(val throwable: Throwable) : Result()
    }
}