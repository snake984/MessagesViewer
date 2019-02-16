package com.messagesviewer.domain.repositories

import com.messagesviewer.domain.model.User
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import java.io.InputStream

interface UserRepository {
    suspend fun fetchUsers(): Deferred<List<User>>
    suspend fun importUsers(users : List<User>): Job
}