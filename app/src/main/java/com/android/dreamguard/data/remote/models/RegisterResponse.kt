package com.android.dreamguard.data.remote.models

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: UserProfile?
)

data class UserProfile(
    @SerializedName("uid") val uid: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("age") val age: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("occupation") val occupation: String?,
    @SerializedName("sleepGoal") val sleepGoal: String?,
    @SerializedName("profilePicture") val profilePicture: String?,
    @SerializedName("createdAt") val createdAt: String
)