package com.messagesviewer.domain.repositories

import com.messagesviewer.domain.model.Attachment
import kotlinx.coroutines.Deferred

interface AttachmentRepository {
    fun fetchAttachments(messageId: Long): Deferred<List<Attachment>>
}