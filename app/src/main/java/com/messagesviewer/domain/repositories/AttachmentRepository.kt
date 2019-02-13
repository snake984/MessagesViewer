package com.messagesviewer.domain.repositories

import com.messagesviewer.domain.model.Attachment
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job

interface AttachmentRepository {
    fun fetchAttachments(messageId: Long): Deferred<List<Attachment>>
    fun saveAttachments(messageId: Long, attachments: List<Attachment>): Job
}