package com.prafullkumar.codeforcesly.common

import android.content.Context
import javax.inject.Inject

class SharedPrefManager @Inject constructor(
    private val context: Context
) {
    companion object {
        const val SHARED_PREF_NAME = "codeforcesly_shared_pref"
        const val LOGGED_IN = "logged_in"
        const val HANDLE = ""
    }
    fun setHandle(value: String) {
        val editor = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(HANDLE, value)
        editor.apply()
    }
    fun getHandle(): String? {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(HANDLE, "")
    }
    fun setLoggedIn(value: Boolean) {
        val editor = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(LOGGED_IN, value)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(LOGGED_IN, false)
    }
}