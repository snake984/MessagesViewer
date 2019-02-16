package com.messagesviewer.domain.repositories

import com.messagesviewer.application.MessagesViewerApplication
import com.messagesviewer.domain.model.Attachment
import com.messagesviewer.domain.model.Message
import com.messagesviewer.remote.db.dao.AttachmentDao
import com.messagesviewer.remote.db.dao.AttachmentDao_Impl
import com.messagesviewer.remote.db.model.AttachmentEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AttachmentRepositoryImpl : AttachmentRepository {
    private val attachmentDao: AttachmentDao = AttachmentDao_Impl(MessagesViewerApplication.database)

    override suspend fun fetchAttachments(messageId: Long) =
        runBlocking {
            async {
                attachmentDao.getAttachments(messageId)
                    .map {
                        Attachment(
                            id = it.id,
                            title = it.title,
                            url = it.url,
                            thumbnailUrl = it.thumbnailUrl
                        )
                    }
            }
        }

    override suspend fun saveAttachments(messageId: Long, attachments: List<Attachment>): Job =
        runBlocking {
            launch {
                attachmentDao.saveAttachments(
                    attachments.map {
                        AttachmentEntity(
                            id = it.id,
                            messageId = messageId,
                            title = it.title,
                            url = it.url,
                            thumbnailUrl = it.thumbnailUrl
                        )
                    }
                )
            }
        }

    override suspend fun deleteAttachment(attachment: Attachment): Job =
        runBlocking {
            launch {
                attachmentDao.deleteAttachment(attachment.id)
            }
        }

    override suspend fun deleteAttachments(message: Message): Job =
        runBlocking {
            launch {
                attachmentDao.deleteAttachments(message.id)
            }
        }
}