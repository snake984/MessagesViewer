package com.messagesviewer.domain.repositories

import com.messagesviewer.domain.model.User
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import java.io.InputStream

interface UserRepository {
    fun fetchUsers(): Deferred<List<User>>
    fun importUsers(source: InputStream): Job
}