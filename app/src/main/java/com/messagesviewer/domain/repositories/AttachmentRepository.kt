package com.messagesviewer.domain.repositories

import com.messagesviewer.domain.model.Attachment
import com.messagesviewer.domain.model.Message
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job

interface AttachmentRepository {
    suspend fun fetchAttachments(messageId: Long): Deferred<List<Attachment>>
    suspend fun saveAttachments(messageId: Long, attachments: List<Attachment>): Job
    suspend fun deleteAttachment(attachment: Attachment): Job
    suspend fun deleteAttachments(message: Message): Job
}