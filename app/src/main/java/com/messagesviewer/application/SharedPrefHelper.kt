package com.messagesviewer.application

import android.content.Context

class SharedPrefHelper {
    fun hasBeenLaunchedOnce(context: Context): Boolean {
        val sharedPreference = context.getSharedPreferences(APP_FIRST_LAUNCH_PREF, Context.MODE_PRIVATE)
        return sharedPreference.getBoolean("hasBeenLaunchedOnce", false)
    }

    fun setFirstLaunchPref(context: Context) {
        val sharedPreference = context.getSharedPreferences(APP_FIRST_LAUNCH_PREF, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putBoolean(HAS_BEEN_LAUNCHED_ONCE_LABEL, true)
        editor.apply()
    }

    companion object {
        private const val APP_FIRST_LAUNCH_PREF = "APP_FIRST_LAUNCH"
        private const val HAS_BEEN_LAUNCHED_ONCE_LABEL = "hasBeenLaunchedOnce"
    }
}