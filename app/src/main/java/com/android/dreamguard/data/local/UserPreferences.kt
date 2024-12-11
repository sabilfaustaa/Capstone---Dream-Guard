package com.android.dreamguard.data.local

import android.content.Context
import android.content.SharedPreferences
import com.android.dreamguard.data.remote.models.UserProfile

class UserPreferences(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "user_prefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_UID = "uid"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_AGE = "user_age"
        private const val KEY_USER_GENDER = "user_gender"
        private const val KEY_USER_OCCUPATION = "user_occupation"
        private const val KEY_USER_SLEEP_GOAL = "user_sleep_goal"
        private const val KEY_PROFILE_PICTURE = "profile_picture"
        private const val KEY_CREATED_AT = "created_at"
    }

    fun saveToken(token: String) {
        preferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return preferences.getString(KEY_TOKEN, null)
    }

    fun saveUserProfile(userProfile: UserProfile) {
        preferences.edit().apply {
            putString(KEY_UID, userProfile.uid)
            putString(KEY_USER_EMAIL, userProfile.email)
            putString(KEY_USER_NAME, userProfile.name)
            putString(KEY_USER_AGE, userProfile.age)
            putString(KEY_USER_GENDER, userProfile.gender)
            putString(KEY_USER_OCCUPATION, userProfile.occupation)
            putString(KEY_USER_SLEEP_GOAL, userProfile.sleepGoal)
            putString(KEY_PROFILE_PICTURE, userProfile.profilePicture)
            putString(KEY_CREATED_AT, userProfile.createdAt)
            apply()
        }
    }

    fun getUserProfile(): UserProfile {
        val uid = preferences.getString(KEY_UID, "") ?: ""
        return UserProfile(
            uid = uid,
            email = preferences.getString(KEY_USER_EMAIL, "") ?: "",
            name = preferences.getString(KEY_USER_NAME, "") ?: "",
            age = preferences.getString(KEY_USER_AGE, "0"),
            gender = preferences.getString(KEY_USER_GENDER, "male"),
            occupation = preferences.getString(KEY_USER_OCCUPATION, "Unknown"),
            sleepGoal = preferences.getString(KEY_USER_SLEEP_GOAL, "08:00 AM"),
            profilePicture = preferences.getString(KEY_PROFILE_PICTURE, ""),
            createdAt = preferences.getString(KEY_CREATED_AT, "") ?: ""
        )
    }


    fun clear() {
        preferences.edit().clear().apply()
    }
}
