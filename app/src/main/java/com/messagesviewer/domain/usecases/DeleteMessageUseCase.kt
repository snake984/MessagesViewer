package com.messagesviewer.domain.usecases

import com.messagesviewer.domain.model.Message
import com.messagesviewer.domain.repositories.MessageRepository
import com.messagesviewer.domain.repositories.MessageRepositoryImpl

class DeleteMessageUseCase {
    private val messageRepository: MessageRepository = MessageRepositoryImpl()

    suspend fun deleteMessage(message: Message): Result =
        try {
            messageRepository.deleteMessage(message)
            Result.Success
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }

    sealed class Result {
        object Success : Result()
        data class Error(val error: Throwable) : Result()
    }
}