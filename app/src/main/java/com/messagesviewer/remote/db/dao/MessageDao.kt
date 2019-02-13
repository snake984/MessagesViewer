package com.messagesviewer.remote.db.dao

import androidx.room.*
import com.messagesviewer.remote.db.model.MessageEntity

@Dao
abstract class MessageDao {

    @Query("SELECT * FROM messages")
    abstract fun getMessages(): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE userId IS :userId")
    abstract fun getMessages(userId: Long): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveMessage(messageEntity: MessageEntity)

    @Transaction
    open fun saveMessages(messages: List<MessageEntity>) {
        messages.forEach { saveMessage(it) }
    }
}