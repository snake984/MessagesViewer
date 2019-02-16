package com.messagesviewer.domain.repositories

import com.messagesviewer.application.MessagesViewerApplication
import com.messagesviewer.domain.model.User
import com.messagesviewer.remote.db.dao.UserDao
import com.messagesviewer.remote.db.dao.UserDao_Impl
import com.messagesviewer.remote.db.model.UserEntity
import kotlinx.coroutines.*

class UserRepositoryImpl : UserRepository {
    private val userDao: UserDao = UserDao_Impl(MessagesViewerApplication.database)

    override suspend fun fetchUsers(): Deferred<List<User>> =
        CoroutineScope(Dispatchers.IO).async {
            userDao.getUsers()
                .map {
                    User(
                        id = it.id,
                        name = it.name,
                        avatarUrl = it.avatarUrl
                    )
                }
        }

    override suspend fun importUsers(users: List<User>) =
        CoroutineScope(Dispatchers.IO).launch {
            userDao.saveUsers(
                users
                    .map {
                        UserEntity(
                            id = it.id,
                            name = it.name,
                            avatarUrl = it.avatarUrl
                        )
                    }
            )
        }
}