package com.messagesviewer.remote.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attachments")
data class AttachmentEntity(
    @PrimaryKey
    var id: String,
    var messageId: Long,
    var title: String,
    var url: String,
    var thumbnailUrl: String
)