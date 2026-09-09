package com.prafullkumar.codeforcesly.common

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SharedPrefManager @Inject constructor(
    private val context: Context
) {
    companion object {
        const val SHARED_PREF_NAME = "codeforcesly_shared_pref"
        const val LOGGED_IN = "logged_in"
        const val HANDLE = "handle"
        const val THEME_MODE = "theme_mode"
        private const val LEGACY_HANDLE = ""
    }

    private val _themeMode = MutableStateFlow(readThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setHandle(value: String) {
        val editor = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(HANDLE, value)
        editor.remove(LEGACY_HANDLE)
        editor.apply()
    }

    fun getHandle(): String? {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(HANDLE, null)
            ?: sharedPreferences.getString(LEGACY_HANDLE, "")
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

    fun setThemeMode(mode: ThemeMode) {
        context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(THEME_MODE, mode.name)
            .apply()
        _themeMode.value = mode
    }

    private fun readThemeMode(): ThemeMode {
        val storedMode = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            .getString(THEME_MODE, null)
        return storedMode?.let { value ->
            runCatching { ThemeMode.valueOf(value) }.getOrNull()
        } ?: ThemeMode.SYSTEM
    }
}
