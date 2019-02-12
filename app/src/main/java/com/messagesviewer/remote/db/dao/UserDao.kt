package com.messagesviewer.remote.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.messagesviewer.remote.db.model.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getUser(): List<UserEntity>

    @Query("SELECT * FROM users WHERE id IS :userId")
    fun getUser(userId: Long): UserEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUser(userEntity: UserEntity)
}