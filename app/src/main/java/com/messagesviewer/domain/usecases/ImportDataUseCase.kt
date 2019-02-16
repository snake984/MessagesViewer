package com.messagesviewer.domain.usecases

import com.messagesviewer.domain.repositories.MessageRepository
import com.messagesviewer.domain.repositories.MessageRepositoryImpl
import com.messagesviewer.domain.repositories.UserRepository
import com.messagesviewer.domain.repositories.UserRepositoryImpl
import com.messagesviewer.remote.util.LocalSourceHelper
import java.io.InputStream

class ImportDataUseCase {
    private val userRepository: UserRepository = UserRepositoryImpl()
    private val messageRepository: MessageRepository = MessageRepositoryImpl()
    private val localSourceHelper = LocalSourceHelper()

    suspend fun import(source: InputStream): Result =
        try {
            val parsedData = localSourceHelper.parseMessages(source)
            userRepository.importUsers(parsedData.users)
            messageRepository.importMessages(parsedData.messages)
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