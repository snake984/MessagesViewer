package com.messagesviewer.domain.repositories

import com.messagesviewer.application.MessagesViewerApplication
import com.messagesviewer.domain.model.Attachment
import com.messagesviewer.remote.db.dao.AttachmentDao
import com.messagesviewer.remote.db.dao.AttachmentDao_Impl
import com.messagesviewer.remote.db.model.AttachmentEntity
import kotlinx.coroutines.*

class AttachmentRepositoryImpl : AttachmentRepository {
    private val attachmentDao: AttachmentDao = AttachmentDao_Impl(MessagesViewerApplication.database)

    override fun fetchAttachments(messageId: Long) =
        CoroutineScope(Dispatchers.IO).async {
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

    override fun saveAttachments(messageId: Long, attachments: List<Attachment>): Job =
        CoroutineScope(Dispatchers.IO).launch {
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