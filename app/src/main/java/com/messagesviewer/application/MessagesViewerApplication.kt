package com.messagesviewer.application

import android.app.Application
import androidx.room.Room
import com.messagesviewer.remote.db.MessagesViewerDatabase

class MessagesViewerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, MessagesViewerDatabase::class.java, "messages-viewer-db").build()
    }

    companion object {
        lateinit var database: MessagesViewerDatabase
    }
}