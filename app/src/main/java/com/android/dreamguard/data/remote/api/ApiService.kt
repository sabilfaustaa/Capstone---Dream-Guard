package com.android.dreamguard.data.remote.api

import com.android.dreamguard.data.remote.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/user/register")
    suspend fun registerNewUser(
        @Header("Authorization") authorization: String,
        @Body requestBody: Map<String, String>
    ): Response<RegisterResponse>

    @GET("api/models/latest")
    suspend fun fetchLatestModel(): Response<LatestModelResponse>
}
