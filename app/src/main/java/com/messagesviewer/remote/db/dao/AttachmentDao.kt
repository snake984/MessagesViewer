package com.messagesviewer.remote.db.dao

import androidx.room.*
import com.messagesviewer.remote.db.model.AttachmentEntity

@Dao
abstract class AttachmentDao {

    @Query("SELECT * FROM attachments")
    abstract fun getAttachments(): List<AttachmentEntity>

    @Query("SELECT * FROM attachments WHERE messageId IS :messageId")
    abstract fun getAttachments(messageId: Long): List<AttachmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveAttachment(attachmentEntity: AttachmentEntity)

    @Transaction
    open fun saveAttachments(attachments: List<AttachmentEntity>) {
        attachments.forEach { saveAttachment(it) }
    }
}