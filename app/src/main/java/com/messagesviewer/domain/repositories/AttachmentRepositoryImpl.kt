package com.messagesviewer.domain.repositories

import com.messagesviewer.application.MessagesViewerApplication
import com.messagesviewer.domain.model.Attachment
import com.messagesviewer.remote.db.dao.AttachmentDao_Impl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class AttachmentRepositoryImpl : AttachmentRepository {
    private val attachmentDao = AttachmentDao_Impl(MessagesViewerApplication.database)

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
}