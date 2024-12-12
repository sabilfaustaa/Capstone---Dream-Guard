package com.android.dreamguard.data.repository

import com.android.dreamguard.data.remote.api.ApiService
import retrofit2.Response

class FeedbackRepository(private val apiService: ApiService) {
    suspend fun addFeedback(feedback: String): Response<Unit> {
        val requestBody = mapOf("feedback" to feedback)
        return apiService.addFeedback(requestBody)
    }
}
