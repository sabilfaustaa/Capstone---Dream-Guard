package com.android.dreamguard.data.local

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class UserPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUserToken(token: String) {
        sharedPreferences.edit().putString("user_token", token).apply()
    }

    fun getUserToken(): String? {
        return sharedPreferences.getString("user_token", null)
    }

    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }
}
