package com.messagesviewer.remote.util

import com.google.gson.Gson
import com.messagesviewer.domain.model.Message
import com.messagesviewer.domain.model.User
import java.io.InputStream

class LocalSourceHelper {
    private val gson = Gson()

    fun parseMessages(inputStream: InputStream): List<Message> {
        val newJsonReader = gson.newJsonReader(inputStream.bufferedReader())
        newJsonReader.beginObject()
        newJsonReader.nextName()
        return gson.fromJson<Array<Message>>(newJsonReader, Array<Message>::class.java).toList()
    }

    fun parseUsers(inputStream: InputStream): List<User> {
        val newJsonReader = gson.newJsonReader(inputStream.bufferedReader())
        newJsonReader.beginObject()
        newJsonReader.nextName()
        newJsonReader.skipValue()
        newJsonReader.nextName()
        return gson.fromJson<Array<User>>(newJsonReader, Array<User>::class.java).toList()
    }
}