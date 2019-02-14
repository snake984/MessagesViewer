package com.messagesviewer.domain.model

import com.google.gson.annotations.SerializedName

data class User(val id: Long, val name: String, @SerializedName("avatarId") val avatarUrl: String)