package com.messagesviewer.view

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.messagesviewer.domain.model.Attachment
import com.messagesviewer.domain.model.Message
import com.messagesviewer.domain.model.User
import com.messagesviewer.domain.usecases.DeleteAttachmentUseCase
import com.messagesviewer.domain.usecases.DeleteMessageUseCase
import com.messagesviewer.domain.usecases.FetchMessagesUseCase
import com.messagesviewer.domain.usecases.ImportDataUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.InputStream
import kotlin.coroutines.CoroutineContext

class MainViewModel : ViewModel(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val _items = MutableLiveData<List<Parcelable>>()
    val items: LiveData<List<Parcelable>> = _items

    private val _messageDeleted = MutableLiveData<MessageItem>()
    val messageDeleted: LiveData<MessageItem> = _messageDeleted

    private val _attachmentDeleted = MutableLiveData<AttachmentItem>()
    val attachmentDeleted: LiveData<AttachmentItem> = _attachmentDeleted

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error

    private val fetchMessagesUseCase = FetchMessagesUseCase()
    private val importDataUseCase = ImportDataUseCase()
    private val deleteMessageUseCase = DeleteMessageUseCase()
    private val deleteAttachmentUseCase = DeleteAttachmentUseCase()

    fun onFirstLaunch(dataSourceFromRawFile: InputStream) {
        launch {
            _isLoading.postValue(true)
            val result = importDataUseCase.run(dataSourceFromRawFile)

            when (result) {
                is ImportDataUseCase.Result.Success -> fetchMessages()
                is ImportDataUseCase.Result.Error -> {
                    _isLoading.postValue(false)
                    _error.postValue(result.throwable)
                }
            }
        }
    }


    fun fetchMessages() {
        launch {
            _isLoading.postValue(true)
            val result = fetchMessagesUseCase.run()

            _isLoading.postValue(false)
            when (result) {
                is FetchMessagesUseCase.Result.Data -> {
                    val fetchedMessages = result.messages
                    val users = result.users
                    val mappedItems = ArrayList<Parcelable>()
                    fetchedMessages.forEach { message ->
                        mappedItems.add(mapToMessageItem(
                            message,
                            users.find { it.id == message.userId } ?: UNKNOWN_USER))

                        message.attachments?.let {
                            it.forEach { attachment ->
                                mappedItems.add(mapToAttachmentItem(attachment, message.userId))
                            }
                        }
                    }
                    _items.postValue(mappedItems)
                }
                is FetchMessagesUseCase.Result.Error -> _error.postValue(result.throwable)
            }
        }
    }

    private fun mapToMessageItem(message: Message, user: User) =
        MessageItem(
            id = message.id,
            userId = user.id,
            userName = user.name,
            userAvatarUrl = user.avatarUrl,
            content = message.content,
            attachments = ArrayList(message.attachments?.map {
                mapToAttachmentItem(it, user.id)
            } ?: ArrayList())
        )

    private fun mapToAttachmentItem(attachment: Attachment, userId: Long) =
        AttachmentItem(
            id = attachment.id,
            userId = userId,
            title = attachment.title,
            url = attachment.url,
            thumbnailUrl = attachment.thumbnailUrl
        )

    private fun mapToMessage(message: MessageItem) =
        Message(
            id = message.id,
            userId = message.userId,
            content = message.content,
            attachments = message.attachments.map {
                mapToAttachment(it)
            }
        )

    private fun mapToAttachment(attachment: AttachmentItem) =
        Attachment(
            id = attachment.id,
            url = attachment.url,
            title = attachment.title,
            thumbnailUrl = attachment.thumbnailUrl
        )

    fun deleteMessage(message: MessageItem) {
        launch {
            _isLoading.postValue(true)
            val result = deleteMessageUseCase.run(mapToMessage(message))
            _isLoading.postValue(false)

            when (result) {
                is DeleteMessageUseCase.Result.Success -> _messageDeleted.postValue(message)
                is DeleteMessageUseCase.Result.Error -> _error.postValue(result.error)
            }
        }
    }

    fun deleteAttachment(attachment: AttachmentItem) {
        CoroutineScope(Dispatchers.IO).launch {
            _isLoading.postValue(true)
            val result = deleteAttachmentUseCase.run(mapToAttachment(attachment))
            _isLoading.postValue(false)

            when (result) {
                is DeleteAttachmentUseCase.Result.Success -> _attachmentDeleted.postValue(attachment)
                is DeleteAttachmentUseCase.Result.Error -> _error.postValue(result.error)
            }
        }
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    companion object {
        private val UNKNOWN_USER = User(0, "", "")
    }
}