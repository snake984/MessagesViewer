package com.messagesviewer.remote.db.dao

import androidx.room.*
import com.messagesviewer.remote.db.model.UserEntity

@Dao
abstract class UserDao {
    @Query("SELECT * FROM users")
    abstract fun getUsers(): List<UserEntity>

    @Query("SELECT * FROM users WHERE id IS :userId")
    abstract fun getUser(userId: Long): UserEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveUser(userEntity: UserEntity)

    @Transaction
    open fun saveUsers(users: List<UserEntity>) {
        users.forEach { saveUser(it) }
    }
}