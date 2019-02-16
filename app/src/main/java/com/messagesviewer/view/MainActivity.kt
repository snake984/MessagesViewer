package com.messagesviewer.view

import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.messagesviewer.R
import com.messagesviewer.application.SharedPrefHelper
import com.messagesviewer.view.util.hide
import com.messagesviewer.view.util.show
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry = lifecycleRegistry

    private val state: State = State(ArrayList())
    private lateinit var mainViewModel: MainViewModel
    private lateinit var sharedPrefHelper: SharedPrefHelper
    private val messagesAdapter = MessagesAdapter(
        onMessageLongClick = {
            showDeleteMessageConfirmationDialog(it)

        },
        onAttachmentLongClick = {
            showDeleteAttachmentConfirmationDialog(it)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        sharedPrefHelper = SharedPrefHelper()

        messagesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        messagesRecyclerView.adapter = messagesAdapter
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.messages_margin_divider, null))
        messagesRecyclerView.addItemDecoration(dividerItemDecoration)
        setPagingListener()

        observeData()

        if (savedInstanceState == null) {
            if (sharedPrefHelper.hasBeenLaunchedOnce(this)) {
                mainViewModel.fetchMessages()
            } else {
                mainViewModel.onFirstLaunch(resources.openRawResource(R.raw.data))
                sharedPrefHelper.setFirstLaunchPref(this)
            }
        } else {
            val state = savedInstanceState.getParcelable(STATE) as State
            if (state.messages.isNotEmpty()) {
                messagesAdapter.addMessages(state.messages)
                emptyMessagesText.hide()
            }
        }
    }

    private fun setPagingListener() {
        messagesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                val linearLayoutManager = messagesRecyclerView.layoutManager as LinearLayoutManager
                val isLoading = mainViewModel.isLoading.value ?: false
                val isLastItemReached =
                    linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.itemCount - 1
                if (isLastItemReached && !isLoading) {
                    mainViewModel.fetchMessages()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(STATE, state)
    }

    private fun observeData() {
        mainViewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                mainProgressBar.show()
            } else {
                mainProgressBar.hide()
            }
        })

        mainViewModel.error.observe(this, Observer {
            Toast.makeText(this, getString(R.string.something_wrong_happened), Toast.LENGTH_SHORT).show()
        })

        mainViewModel.messages.observe(this, Observer {
            if (it.isNotEmpty()) {
                emptyMessagesText.hide()
                messagesAdapter.addMessages(it)
                state.messages.addAll(it)
            } else if (messagesAdapter.isEmpty()) {
                emptyMessagesText.show()
            }
        })

        mainViewModel.messageDeleted.observe(this, Observer {
            messagesAdapter.removeMessage(it)
            state.messages.remove(it)
            if (messagesAdapter.isEmpty())
                emptyMessagesText.show()
        })

        mainViewModel.attachmentDeleted.observe(this, Observer { attachmentItem ->
            messagesAdapter.removeAttachment(attachmentItem)
            val messageItem = state.messages.find { it.attachments.contains(attachmentItem) }
            messageItem?.attachments?.remove(attachmentItem)
        })
    }

    private fun showDeleteMessageConfirmationDialog(messageItem: MessageItem) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_message_title))
            .setMessage(getString(R.string.delete_message_label))
            .setPositiveButton(android.R.string.yes) { _, _ ->
                mainViewModel.deleteMessage(messageItem)
            }
            .setNegativeButton(android.R.string.no) { _, _ -> Unit }
            .show()
    }

    private fun showDeleteAttachmentConfirmationDialog(attachmentItem: AttachmentItem) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_attachment_title))
            .setMessage(getString(R.string.delete_attachment_label))
            .setPositiveButton(android.R.string.yes) { _, _ ->
                mainViewModel.deleteAttachment(attachmentItem)
            }
            .setNegativeButton(android.R.string.no) { _, _ -> Unit }
            .show()
    }

    @Parcelize
    data class State(val messages: ArrayList<MessageItem>) : Parcelable

    companion object {
        private const val STATE = "main-activity-state"
    }
}
