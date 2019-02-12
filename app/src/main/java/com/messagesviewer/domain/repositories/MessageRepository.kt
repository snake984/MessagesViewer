package com.messagesviewer.domain.repositories

import com.messagesviewer.domain.model.Message
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import java.io.InputStream

interface MessageRepository {
    fun fetchMessages(userId: Long): Deferred<List<Message>>
    fun importMessages(source: InputStream): Job
}