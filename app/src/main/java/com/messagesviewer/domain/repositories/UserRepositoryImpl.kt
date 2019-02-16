package com.messagesviewer.domain.repositories

import com.messagesviewer.application.MessagesViewerApplication
import com.messagesviewer.domain.model.User
import com.messagesviewer.remote.db.dao.UserDao
import com.messagesviewer.remote.db.dao.UserDao_Impl
import com.messagesviewer.remote.db.model.UserEntity
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserRepositoryImpl : UserRepository {
    private val userDao: UserDao = UserDao_Impl(MessagesViewerApplication.database)

    override suspend fun fetchUsers(): Deferred<List<User>> =
        runBlocking {
            async {
                userDao.getUsers()
                    .map {
                        User(
                            id = it.id,
                            name = it.name,
                            avatarUrl = it.avatarUrl
                        )
                    }
            }
        }

    override suspend fun importUsers(users: List<User>) =
        runBlocking {
            launch {
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
}