package com.messagesviewer.domain.repositories

import com.messagesviewer.application.MessagesViewerApplication
import com.messagesviewer.domain.model.Message
import com.messagesviewer.remote.db.dao.MessageDao
import com.messagesviewer.remote.db.dao.MessageDao_Impl
import com.messagesviewer.remote.db.model.MessageEntity
import com.messagesviewer.remote.util.LocalSourceHelper
import kotlinx.coroutines.*
import java.io.InputStream

class MessageRepositoryImpl : MessageRepository {

    private val localSourceHelper = LocalSourceHelper()
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

    override suspend fun importMessages(source: InputStream): Job =
        CoroutineScope(Dispatchers.IO).launch {
            localSourceHelper.parseMessages(source)
                .forEach {
                    messageDao.saveMessage(mapMessageToEntity(it))
                    attachmentRepository.saveAttachments(
                        it.id,
                        it.attachments
                    )
                }
        }

    private fun mapMessageToEntity(message: Message): MessageEntity =
        MessageEntity(
            id = message.id,
            userId = message.userId,
            content = message.content
        )
}