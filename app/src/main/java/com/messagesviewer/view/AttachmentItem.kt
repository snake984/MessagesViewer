package com.messagesviewer.view

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AttachmentItem(
    val title: String,
    val url: String,
    val thumbnailUrl: String
) : Parcelable