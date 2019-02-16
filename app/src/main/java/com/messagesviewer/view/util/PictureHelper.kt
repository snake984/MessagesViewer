package com.messagesviewer.view.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

class PictureHelper {
    companion object {
        fun setupImageView(context: Context, url: String, imageView: ImageView) =
            Glide.with(context)
                .asDrawable()
                .load(url)
                .into(imageView)
    }
}