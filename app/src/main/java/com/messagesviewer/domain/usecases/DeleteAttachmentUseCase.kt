package com.messagesviewer.domain.usecases

import com.messagesviewer.domain.model.Attachment
import com.messagesviewer.domain.repositories.AttachmentRepository
import com.messagesviewer.domain.repositories.AttachmentRepositoryImpl

class DeleteAttachmentUseCase {
    private val attachmentRepository: AttachmentRepository = AttachmentRepositoryImpl()

    fun deleteAttachment(attachment: Attachment) =
        try {
            attachmentRepository.deleteAttachment(attachment)
            Result.Success
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }

    sealed class Result {
        object Success : Result()
        data class Error(val error: Throwable) : Result()
    }
}