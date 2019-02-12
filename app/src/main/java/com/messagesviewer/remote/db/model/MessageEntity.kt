package com.messagesviewer.remote.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
        @PrimaryKey
        var id: Long,
        var userId: Long,
        var content: String
)