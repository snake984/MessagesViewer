package com.messagesviewer.view

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

class MainViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<MessageItem>>()
    val messages: LiveData<List<MessageItem>> = _messages

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
    private val jobs = ArrayList<Job>()

    fun onFirstLaunch(dataSourceFromRawFile: InputStream) {
        dispose()
        jobs.add(CoroutineScope(Dispatchers.IO).launch {
            _isLoading.postValue(true)
            val result = importDataUseCase.import(dataSourceFromRawFile)

            when (result) {
                is ImportDataUseCase.Result.Success -> fetchMessages()
                is ImportDataUseCase.Result.Error -> {
                    _isLoading.postValue(false)
                    _error.postValue(result.throwable)
                }
            }
        })
    }


    fun fetchMessages() {
        dispose()
        jobs.add(CoroutineScope(Dispatchers.IO).launch {
            _isLoading.postValue(true)
            val result = fetchMessagesUseCase.fetchMessages()

            _isLoading.postValue(false)
            when (result) {
                is FetchMessagesUseCase.Result.Data -> {
                    val fetchedMessages = result.messages
                    val users = result.users
                    _messages.postValue(
                        fetchedMessages.map { message ->
                            val user = users.find { it.id == message.userId } ?: UNKNOWN_USER
                            mapToMessageItem(message, user)
                        }
                    )
                }
                is FetchMessagesUseCase.Result.Error -> _error.postValue(result.throwable)
            }
        })
    }

    private fun mapToMessageItem(message: Message, user: User) =
        MessageItem(
            id = message.id,
            userId = user.id,
            userName = user.name,
            userAvatarUrl = user.avatarUrl,
            content = message.content,
            attachments = ArrayList(message.attachments?.map {
                mapToAttachmentItem(it)
            } ?: ArrayList())
        )

    private fun mapToAttachmentItem(attachment: Attachment) =
        AttachmentItem(
            id = attachment.id,
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

    private fun dispose() = with(jobs) {
        forEach { it.cancel() }
        clear()
    }

    fun onMessageLongClick(message: MessageItem) {
        dispose()
        jobs.add(
            CoroutineScope(Dispatchers.IO).launch {
                _isLoading.postValue(true)
                val result = deleteMessageUseCase.deleteMessage(mapToMessage(message))
                _isLoading.postValue(false)

                when (result) {
                    is DeleteMessageUseCase.Result.Success -> _messageDeleted.postValue(message)
                    is DeleteMessageUseCase.Result.Error -> _error.postValue(result.error)
                }
            }
        )
    }

    fun onAttachmentLongClick(attachment: AttachmentItem) {
        dispose()
        jobs.add(
            CoroutineScope(Dispatchers.IO).launch {
                _isLoading.postValue(true)
                val result = deleteAttachmentUseCase.deleteAttachment(mapToAttachment(attachment))
                _isLoading.postValue(false)

                when (result) {
                    is DeleteAttachmentUseCase.Result.Success -> _attachmentDeleted.postValue(attachment)
                    is DeleteAttachmentUseCase.Result.Error -> _error.postValue(result.error)
                }
            }
        )
    }

    companion object {
        private val UNKNOWN_USER = User(0, "", "")
    }
}