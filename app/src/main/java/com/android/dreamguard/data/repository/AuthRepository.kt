package com.android.dreamguard.data.repository

import com.android.dreamguard.data.remote.api.ApiService
import com.android.dreamguard.data.remote.models.RegisterResponse
import retrofit2.Response
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(private val apiService: ApiService) {
    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun registerNewUser(token: String, email: String, name: String): Response<RegisterResponse> {
        val requestBody = mapOf(
            "email" to email,
            "name" to name
        )
        return apiService.registerNewUser(requestBody)
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Boolean {
        val user = firebaseAuth.currentUser ?: throw Exception("User not logged in")
        val email = user.email ?: throw Exception("Email not found")

        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential).await()

        user.updatePassword(newPassword).await()
        return true
    }

}
