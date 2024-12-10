package com.android.dreamguard.data.remote.api

import com.android.dreamguard.data.remote.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/user/register")
    suspend fun registerNewUser(
        @Body requestBody: Map<String, String>
    ): Response<RegisterResponse>

    @POST("api/user/predictions")
    suspend fun addNewPrediction(
        @Body predictionData: Map<String, Int>
    ): Response<PredictionResponse>

    @GET("api/user/predictions")
    suspend fun getPredictionHistory(): Response<PredictionHistoryResponse>

}
