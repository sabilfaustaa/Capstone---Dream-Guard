package com.android.dreamguard.data.repository

import com.android.dreamguard.data.remote.api.ApiService
import com.android.dreamguard.data.remote.models.RegisterResponse
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    suspend fun registerNewUser(token: String, email: String, name: String): Response<RegisterResponse> {
        val requestBody = mapOf(
            "email" to email,
            "name" to name
        )
        return apiService.registerNewUser(requestBody)
    }

}
