package com.android.dreamguard.data.repository

import com.android.dreamguard.data.remote.api.ApiService
import com.android.dreamguard.data.remote.models.PredictionResponse
import retrofit2.Response

class PredictionRepository(private val apiService: ApiService) {

    suspend fun submitPrediction(predictionData: Map<String, Int>): Response<PredictionResponse> {
        return apiService.addNewPrediction(predictionData)
    }
}
