package com.messagesviewer.domain.repositories

import com.messagesviewer.domain.model.Message
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import java.io.InputStream

interface MessageRepository {
    suspend fun fetchMessages(pageSize : Int): Deferred<List<Message>>
    suspend fun importMessages(source: InputStream): Job
}