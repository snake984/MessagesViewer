package com.messagesviewer.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.messagesviewer.R
import com.messagesviewer.view.util.PictureHelper
import kotlinx.android.synthetic.main.attachment_view.view.*
import kotlinx.android.synthetic.main.my_message_item_view.view.*
import kotlinx.android.synthetic.main.others_message_item_view.view.*

class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>() {
    private val messages: ArrayList<MessageItem> = ArrayList()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder =
        when (viewType) {
            MY_MESSAGES_VIEW_TYPE -> {
                MessagesViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.my_message_item_view,
                        parent,
                        false
                    )
                )
            }
            OTHERS_MESSAGES_VIEW_TYPE -> {
                MessagesViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.others_message_item_view,
                        parent,
                        false
                    )
                )
            }
            else -> throw IllegalArgumentException("ViewType should be $MY_MESSAGES_VIEW_TYPE or $OTHERS_MESSAGES_VIEW_TYPE")
        }

    override fun getItemCount(): Int = messages.size

    override fun getItemId(position: Int): Long = messages[position].hashCode().toLong()

    override fun getItemViewType(position: Int): Int =
        if (messages[position].userId == 1L) MY_MESSAGES_VIEW_TYPE
        else OTHERS_MESSAGES_VIEW_TYPE

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) =
        holder.bind(messages[position], getItemViewType(position))

    fun addMessages(messageItems: List<MessageItem>) {
        messages.addAll(messageItems)
        notifyItemRangeInserted(messages.size, messageItems.size)
    }

    fun isEmpty(): Boolean = itemCount == 0

    class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(messageItem: MessageItem, viewType: Int) {
            when (viewType) {
                MY_MESSAGES_VIEW_TYPE -> {
                    itemView.myUserName.text = itemView.context.getText(R.string.me)
                    itemView.myMessageContent.text = messageItem.content
                    if (messageItem.attachments.isNotEmpty()) {
                        setupAttachments(itemView.myAttachmentsContainer, messageItem.attachments)
                    } else {
                        itemView.myAttachmentsContainer.removeAllViews()
                    }
                }
                OTHERS_MESSAGES_VIEW_TYPE -> {
                    itemView.userName.text = messageItem.userName
                    itemView.messageContent.text = messageItem.content
                    PictureHelper.setupImageView(
                        itemView.context,
                        messageItem.userAvatarUrl,
                        itemView.userAvatar
                    )
                    if (messageItem.attachments.isNotEmpty()) {
                        setupAttachments(itemView.attachmentsContainer, messageItem.attachments)
                    } else {
                        itemView.attachmentsContainer.removeAllViews()
                    }
                }
            }
        }

        private fun setupAttachments(container: ViewGroup, attachments: List<AttachmentItem>) {
            attachments.forEach {
                val attachmentView = AttachmentView(itemView.context)
                attachmentView.attachmentName.text = it.title
                PictureHelper.setupImageView(
                    itemView.context,
                    it.thumbnailUrl,
                    attachmentView.attachmentImage
                )
                container.addView(attachmentView)
            }
        }
    }

    companion object {
        const val MY_MESSAGES_VIEW_TYPE = 0
        const val OTHERS_MESSAGES_VIEW_TYPE = 1
    }
}