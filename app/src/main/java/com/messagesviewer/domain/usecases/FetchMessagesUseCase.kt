package com.messagesviewer.domain.usecases

import com.messagesviewer.domain.model.Message
import com.messagesviewer.domain.model.User
import com.messagesviewer.domain.repositories.MessageRepository
import com.messagesviewer.domain.repositories.MessageRepositoryImpl
import com.messagesviewer.domain.repositories.UserRepository
import com.messagesviewer.domain.repositories.UserRepositoryImpl

class FetchMessagesUseCase {
    private val messagesRepository: MessageRepository = MessageRepositoryImpl()
    private val userRepository: UserRepository = UserRepositoryImpl()

    suspend fun fetchMessages(): Result =
        try {
            Result.Data(
                userRepository.fetchUsers().await(),
                messagesRepository.fetchMessages(HITS_BY_PAGE).await().sortedBy { it.id }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }

    sealed class Result {
        data class Data(val users: List<User>, val messages: List<Message>) : Result()
        data class Error(val throwable: Throwable) : Result()
    }

    companion object {
        const val HITS_BY_PAGE = 20
    }
}