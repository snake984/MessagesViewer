package com.messagesviewer.view

import android.os.Bundle
import android.widget.Toast
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
import kotlinx.android.synthetic.main.activity_main.*

//TODO - Faire du paging avec la librairie Pager de Google !!
class MainActivity : AppCompatActivity(), LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry = lifecycleRegistry

    private lateinit var mainViewModel: MainViewModel
    private lateinit var sharedPrefHelper: SharedPrefHelper
    private val messagesAdapter = MessagesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        messagesRecyclerView.adapter = messagesAdapter
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.messages_margin_divider, null))
        messagesRecyclerView.addItemDecoration(dividerItemDecoration)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        sharedPrefHelper = SharedPrefHelper()
        observeData()
        if (sharedPrefHelper.hasBeenLaunchedOnce(this)) {
            mainViewModel.fetchMessages()
        } else {
            mainViewModel.onFirstLaunch(resources.openRawResource(R.raw.data))
            sharedPrefHelper.setFirstLaunchPref(this)
        }
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
            //TODO - Better error handling
            Toast.makeText(this, getString(R.string.something_wrong_happened), Toast.LENGTH_SHORT).show()
        })

        mainViewModel.messages.observe(this, Observer {
            //TODO - Fill list with data (don't forget that you get a new page)
            if (it.isNotEmpty()) {
                emptyMessagesText.hide()
                messagesAdapter.addMessages(it)
            } else if (messagesAdapter.isEmpty()) {
                emptyMessagesText.show()
            }
        })
    }
}
