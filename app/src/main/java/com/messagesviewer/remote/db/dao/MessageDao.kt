package com.messagesviewer.remote.db.dao

import androidx.room.*
import com.messagesviewer.remote.db.model.MessageEntity

@Dao
abstract class MessageDao {

    @Query("SELECT * FROM messages LIMIT :startRow, :hitsNbr")
    abstract fun getMessages(startRow: Int, hitsNbr: Int): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveMessage(messageEntity: MessageEntity)

    @Transaction
    open fun saveMessages(messages: List<MessageEntity>) {
        messages.forEach { saveMessage(it) }
    }

    @Query("DELETE FROM messages WHERE id IS :id")
    abstract fun deleteMessage(id: Long)
}