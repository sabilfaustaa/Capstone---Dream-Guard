package com.android.dreamguard.data.repository

import com.android.dreamguard.data.remote.api.ApiService
import com.android.dreamguard.data.remote.models.LatestModelResponse
import retrofit2.Response

class ModelRepository(private val apiService: ApiService) {

    suspend fun getLatestModelUrl(): Response<LatestModelResponse> {
        return apiService.fetchLatestModel()
    }
}
