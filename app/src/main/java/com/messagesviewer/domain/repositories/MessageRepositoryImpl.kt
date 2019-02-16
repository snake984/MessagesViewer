package com.messagesviewer.domain.repositories

import com.messagesviewer.application.MessagesViewerApplication
import com.messagesviewer.domain.model.Message
import com.messagesviewer.remote.db.dao.MessageDao
import com.messagesviewer.remote.db.dao.MessageDao_Impl
import com.messagesviewer.remote.db.model.MessageEntity
import kotlinx.coroutines.*

class MessageRepositoryImpl : MessageRepository {

    private val messageDao: MessageDao = MessageDao_Impl(MessagesViewerApplication.database)
    private val attachmentRepository: AttachmentRepository = AttachmentRepositoryImpl()
    private var currentPage = 0

    override suspend fun fetchMessages(pageSize: Int): Deferred<List<Message>> =
        CoroutineScope(Dispatchers.IO).async {
            messageDao.getMessages(currentPage * pageSize, pageSize)
                .map {
                    Message(
                        id = it.id,
                        userId = it.userId,
                        content = it.content,
                        attachments = attachmentRepository.fetchAttachments(it.id).await()
                    )
                }.also { currentPage++ }
        }

    override suspend fun importMessages(messages: List<Message>): Job =
        CoroutineScope(Dispatchers.IO).launch {
            messages
                .forEach { message ->
                    messageDao.saveMessage(mapMessageToEntity(message))
                    message.attachments?.let {
                        attachmentRepository.saveAttachments(
                            message.id,
                            it
                        )
                    }
                }
        }

    private fun mapMessageToEntity(message: Message): MessageEntity =
        MessageEntity(
            id = message.id,
            userId = message.userId,
            content = message.content
        )
}