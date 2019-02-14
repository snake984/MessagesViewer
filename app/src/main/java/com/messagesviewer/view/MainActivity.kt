package com.messagesviewer.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.messagesviewer.R

//TODO - Faire le layout de l'activity avec toolbar et recyclerview dedans
//TODO - Faire le layout de chaque item (avatar + message + view qui contient l'attachment)
//TODO - Faire du paging avec la librairie Pager de Google !!
class MainActivity : AppCompatActivity(), LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry = lifecycleRegistry

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        observeData()
    }

    private fun observeData() {
        mainViewModel.isLoading.observe(this, Observer {
            //TODO - Show/hide progress bar
        })

        mainViewModel.error.observe(this, Observer {
            //TODO - Show error message
        })

        mainViewModel.messages.observe(this, Observer {
            //TODO - Fill list with data (don't forget that you get a new page)
        })
    }
}
