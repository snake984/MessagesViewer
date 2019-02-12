package com.messagesviewer.domain.repositories

import com.messagesviewer.application.MessagesViewerApplication
import com.messagesviewer.domain.model.Message
import com.messagesviewer.remote.db.dao.MessageDao_Impl
import com.messagesviewer.remote.util.LocalSourceHelper
import kotlinx.coroutines.*
import java.io.InputStream

class MessageRepositoryImpl : MessageRepository {

    private val localSourceHelper = LocalSourceHelper()
    private val messageDao = MessageDao_Impl(MessagesViewerApplication.database)
    private val attachmentRepository = AttachmentRepositoryImpl()

    override fun fetchMessages(userId: Long): Deferred<List<Message>> =
        CoroutineScope(Dispatchers.IO).async {
            messageDao.getMessages(userId)
                .map {
                    Message(
                        id = it.id,
                        userId = it.userId,
                        content = it.content,
                        attachments = attachmentRepository.fetchAttachments(it.id).await()
                    )
                }
        }

    override fun importMessages(source: InputStream): Job =
        CoroutineScope(Dispatchers.IO).launch {
            localSourceHelper.parseMessages(source)
        }

}