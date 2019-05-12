package com.messagesviewer.view

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.messagesviewer.R
import com.messagesviewer.view.util.PictureHelper
import kotlinx.android.synthetic.main.attachment_view.view.*
import kotlinx.android.synthetic.main.my_message_item_view.view.*
import kotlinx.android.synthetic.main.others_message_item_view.view.*

class MessagesAdapter(
    val onMessageLongClick: (MessageItem) -> (Unit),
    val onAttachmentLongClick: (AttachmentItem) -> (Unit)
) :
    RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>() {
    private val items: ArrayList<Parcelable> = ArrayList()

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
            MY_ATTACHMENT_VIEW_TYPE -> {
                MessagesViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.my_attachment_item_view,
                        parent,
                        false
                    )
                )
            }
            OTHERS_ATTACHMENT_VIEW_TYPE -> {
                MessagesViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.others_attachment_item_view,
                        parent,
                        false
                    )
                )
            }
            else -> throw IllegalArgumentException("ViewType should be $MY_MESSAGES_VIEW_TYPE,  $OTHERS_MESSAGES_VIEW_TYPE or $MY_ATTACHMENT_VIEW_TYPE")
        }

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long = items[position].hashCode().toLong()

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is MessageItem -> {
                val messageItem = items[position] as MessageItem
                if (messageItem.userId == 1L) MY_MESSAGES_VIEW_TYPE
                else OTHERS_MESSAGES_VIEW_TYPE

            }
            is AttachmentItem -> {
                val attachmentItem = items[position] as AttachmentItem
                if (attachmentItem.userId == 1L) {
                    MY_ATTACHMENT_VIEW_TYPE
                } else {
                    OTHERS_ATTACHMENT_VIEW_TYPE
                }
            }
            else -> throw IllegalArgumentException("Item is not of ${MessageItem::class.java} or ${AttachmentItem::class.java}")
        }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) =
        holder.bind(items[position], getItemViewType(position))

    fun addMessages(messageItems: List<Parcelable>) {
        items.addAll(messageItems)
        notifyItemRangeInserted(items.size, messageItems.size)
    }

    fun isEmpty(): Boolean = itemCount == 0

    fun removeMessage(message: MessageItem) {
        val index = items.indexOf(message)
        items.remove(message)
        notifyItemRemoved(index)
        message.attachments.forEach { removeAttachment(it) }
    }

    fun removeAttachment(attachmentItem: AttachmentItem) {
        val index = items.indexOf(attachmentItem)
        items.remove(attachmentItem)
        notifyItemRemoved(index)
    }

    inner class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Parcelable, viewType: Int) {
            when (viewType) {
                MY_MESSAGES_VIEW_TYPE -> {
                    val messageItem = item as MessageItem
                    itemView.myUserName.text = itemView.context.getText(R.string.me)
                    itemView.myMessageContent.text = messageItem.content
                    setMessageLongClickListener(itemView, messageItem)
                }
                OTHERS_MESSAGES_VIEW_TYPE -> {
                    val messageItem = item as MessageItem
                    itemView.userName.text = messageItem.userName
                    itemView.messageContent.text = messageItem.content
                    PictureHelper.setupImageView(
                        itemView.context,
                        messageItem.userAvatarUrl,
                        itemView.userAvatar
                    )
                    setMessageLongClickListener(itemView, messageItem)
                }
                MY_ATTACHMENT_VIEW_TYPE, OTHERS_ATTACHMENT_VIEW_TYPE -> {
                    val attachmentItem = item as AttachmentItem
                    itemView.attachmentName.text = attachmentItem.title
                    PictureHelper.setupImageView(
                        itemView.context,
                        attachmentItem.thumbnailUrl,
                        itemView.attachmentImage
                    )
                    itemView.setOnLongClickListener {
                        onAttachmentLongClick(attachmentItem)
                        true
                    }
                }
            }
        }

        private fun setMessageLongClickListener(view: View, messageItem: MessageItem) {
            view.setOnLongClickListener {
                onMessageLongClick(messageItem)
                true
            }
        }
    }

    companion object {
        const val MY_MESSAGES_VIEW_TYPE = 0
        const val OTHERS_MESSAGES_VIEW_TYPE = 1
        const val MY_ATTACHMENT_VIEW_TYPE = 2
        const val OTHERS_ATTACHMENT_VIEW_TYPE = 3
    }
}