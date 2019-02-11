package com.messagesviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

//TODO - Faire le layout de l'activity avec toolbar et recyclerview dedans
//TODO - Faire le layout de chaque item (avatar + message + view qui contient l'attachment)
//TODO - Faire du paging avec la librairie Pager de Google !!
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
