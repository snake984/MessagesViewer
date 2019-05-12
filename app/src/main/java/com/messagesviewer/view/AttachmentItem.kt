package com.messagesviewer.view

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AttachmentItem(
    val id: String,
    val userId: Long,
    val title: String,
    val url: String,
    val thumbnailUrl: String
) : Parcelable