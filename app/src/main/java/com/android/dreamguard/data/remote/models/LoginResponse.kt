package com.android.dreamguard.data.remote.models

data class LoginResponse(
    val token: String,
    val user: User
)

data class User(
    val id: String,
    val name: String,
    val email: String
)
