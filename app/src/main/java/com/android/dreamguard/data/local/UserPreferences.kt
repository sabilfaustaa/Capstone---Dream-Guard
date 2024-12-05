package com.android.dreamguard.data.local

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "user_prefs"
        private const val KEY_TOKEN = "token"
    }

    fun saveToken(token: String) {
        preferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return preferences.getString(KEY_TOKEN, null)
    }

    fun clear() {
        preferences.edit().clear().apply()
    }
}
