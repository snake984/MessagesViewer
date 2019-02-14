package com.messagesviewer.view

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageItem(
    val id: Long,
    val userId: Long,
    val userName: String,
    val userAvatarUrl: String,
    val content: String,
    val attachments: List<AttachmentItem>
) : Parcelable