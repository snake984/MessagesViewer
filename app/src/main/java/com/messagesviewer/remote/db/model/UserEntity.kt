package com.messagesviewer.remote.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    var id: Long,
    var name: String,
    var avatarUrl: String
)