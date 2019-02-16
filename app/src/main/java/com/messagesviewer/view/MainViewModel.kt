package com.messagesviewer.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.messagesviewer.domain.model.Attachment
import com.messagesviewer.domain.model.Message
import com.messagesviewer.domain.model.User
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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error

    private val fetchMessagesUseCase = FetchMessagesUseCase()
    private val importDataUseCase = ImportDataUseCase()
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


    //TODO - Ajouter l'observation des live data dans l'activity et ne pas oublier de lancer le paging dans l'adapter
    //TODO - Penser que chaque nouveau résultat va aller chercher une nouvelle page
    //TODO - Prier pour que la BDD ne crash pas quand on dépasse l'offset ou limit
    fun fetchMessages() {
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
            attachments = message.attachments?.map {
                mapToAttachmentItem(it)
            } ?: ArrayList()
        )

    private fun mapToAttachmentItem(it: Attachment) =
        AttachmentItem(
            title = it.title,
            url = it.url,
            thumbnailUrl = it.thumbnailUrl
        )

    private fun dispose() = with(jobs) {
        forEach { it.cancel() }
        clear()
    }

    companion object {
        private val UNKNOWN_USER = User(0, "", "")
    }
}