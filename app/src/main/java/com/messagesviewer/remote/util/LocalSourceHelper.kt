package com.messagesviewer.remote.util

import com.google.gson.Gson
import com.messagesviewer.domain.model.Message
import com.messagesviewer.domain.model.User
import java.io.InputStream

class LocalSourceHelper {
    private val gson = Gson()

    fun parseMessages(source: InputStream): ParsedData {
        val newJsonReader = gson.newJsonReader(source.bufferedReader())
        newJsonReader.beginObject()
        newJsonReader.nextName()
        val messages = gson.fromJson<Array<Message>>(newJsonReader, Array<Message>::class.java).toList()
        newJsonReader.nextName()
        val users = gson.fromJson<Array<User>>(newJsonReader, Array<User>::class.java).toList()
        return ParsedData(messages, users)
    }

    data class ParsedData(val messages: List<Message>, val users: List<User>)
}