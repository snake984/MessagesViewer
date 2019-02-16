package com.messagesviewer.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.messagesviewer.R

class AttachmentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.attachment_view, this)
    }
}