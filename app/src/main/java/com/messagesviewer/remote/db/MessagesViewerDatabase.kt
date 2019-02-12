package com.messagesviewer.remote.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.messagesviewer.remote.db.dao.AttachmentDao
import com.messagesviewer.remote.db.dao.MessageDao
import com.messagesviewer.remote.db.dao.UserDao
import com.messagesviewer.remote.db.model.AttachmentEntity
import com.messagesviewer.remote.db.model.MessageEntity
import com.messagesviewer.remote.db.model.UserEntity

@Database(entities = arrayOf(AttachmentEntity::class, MessageEntity::class, UserEntity::class), version = 1)
abstract class MessagesViewerDatabase : RoomDatabase() {
    abstract fun AttachmentDao(): AttachmentDao
    abstract fun MessageDao(): MessageDao
    abstract fun UserDao(): UserDao
}