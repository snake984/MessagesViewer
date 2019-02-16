package com.messagesviewer.domain.repositories

import com.messagesviewer.domain.model.Message
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job

interface MessageRepository {
    suspend fun fetchMessages(pageSize: Int): Deferred<List<Message>>
    suspend fun importMessages(messages: List<Message>): Job
    suspend fun deleteMessage(message: Message): Job
}