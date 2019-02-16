package com.messagesviewer.domain.repositories

import com.messagesviewer.domain.model.Attachment
import com.messagesviewer.domain.model.Message
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job

interface AttachmentRepository {
    fun fetchAttachments(message: Long): Deferred<List<Attachment>>
    fun saveAttachments(messageId: Long, attachments: List<Attachment>): Job
    fun deleteAttachment(attachment: Attachment): Job
    fun deleteAttachments(message: Message): Job
}