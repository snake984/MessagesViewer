package com.messagesviewer.remote.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.messagesviewer.remote.db.model.AttachmentEntity

@Dao
interface AttachmentDao {

    @Query("SELECT * FROM attachments")
    fun getAttachments(): List<AttachmentEntity>

    @Query("SELECT * FROM attachments WHERE messageId IS :messageId")
    fun getAttachments(messageId: Long): List<AttachmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAttachment(attachmentEntity: AttachmentEntity)
}