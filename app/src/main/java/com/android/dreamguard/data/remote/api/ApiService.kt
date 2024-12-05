package com.android.dreamguard.data.remote.api

import com.android.dreamguard.data.remote.models.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("api/user/register")
    suspend fun registerNewUser(
        @Header("Authorization") authorization: String,
        @Body requestBody: Map<String, String>
    ): Response<RegisterResponse>

}
