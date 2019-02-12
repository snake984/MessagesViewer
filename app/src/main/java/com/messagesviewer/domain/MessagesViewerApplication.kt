package com.messagesviewer.domain

import android.app.Application
import androidx.room.Room
import com.messagesviewer.remote.db.MessagesViewerDatabase

class MessagesViewerApplication : Application() {

    private lateinit var database: MessagesViewerDatabase

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, MessagesViewerDatabase::class.java, "messages-viewer-db").build()
    }

    fun getDatabase() = database
}