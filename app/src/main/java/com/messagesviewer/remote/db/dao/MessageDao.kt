package com.messagesviewer.remote.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.messagesviewer.remote.db.model.MessageEntity

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages")
    fun getMessages(): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE userId IS :userId")
    fun getMessages(userId: Long): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMessage(messageEntity: MessageEntity)
}